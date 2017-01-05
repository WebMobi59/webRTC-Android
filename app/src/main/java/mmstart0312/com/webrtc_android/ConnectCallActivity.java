package mmstart0312.com.webrtc_android;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.PercentFrameLayout;
import org.webrtc.RendererCommon;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoTrack;

import java.util.LinkedList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import mmstart0312.com.webrtc_android.classes.CircularSurfaceViewRenderer;
import mmstart0312.com.webrtc_android.classes.SocketIOManager;

public class ConnectCallActivity extends AppCompatActivity implements Emitter.Listener, PeerConnection.Observer{

    private Context context;
    private PeerConnectionFactory peerConnectionFactory;
    private MediaConstraints pcConstraints;
    private MediaConstraints videoConstraints;
    private MediaConstraints audioConstraints;
    private MediaConstraints mediaConstraints;
    private VideoTrack remoteVideoTrack;
    private AudioTrack remoteAudioTrack;
    private AudioTrack localAudioTrack;
    private MediaStream mediaStream;
    private PeerConnection peerConnection;

    private String VIDEO_TRACK_ID = "VIDEO";
    private String AUDIO_TRACK_ID = "AUDIO";
    private String LOCAL_MEDIA_STREAM_ID = "STREAM";

    private SocketIOManager socketIOManager;
    private Socket socket;
    private String wsServerUrl;
    private boolean peerStarted = false;

    private TextView openBtn;
    private ImageButton dropBtn;
    private ImageButton muteBtn;
    private GLSurfaceView remoteUserView;

    private org.webrtc.PercentFrameLayout remoteRenderLayout;
    private CircularSurfaceViewRenderer remoteRender;
    private EglBase rootEglBase;
    private LinearLayout buttonLayout;
    float btnLayoutHeight;
    private String roomID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_call);

        roomID = (getIntent().getExtras() != null)? getIntent().getExtras().getString("roomID") : "12345";

        this.context = this;

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        openBtn = (TextView) findViewById(R.id.open_call_btn);
        dropBtn = (ImageButton) findViewById(R.id.drop_call_btn);
        muteBtn = (ImageButton) findViewById(R.id.mute_call_btn);

        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);

        ViewTreeObserver vto = buttonLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ConnectCallActivity.this.buttonLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width  = ConnectCallActivity.this.buttonLayout.getMeasuredWidth();
                int height = ConnectCallActivity.this.buttonLayout.getMeasuredHeight();
                remoteRender.drawCircleCenter(height);
            }
        });


        remoteRenderLayout = (PercentFrameLayout) findViewById(R.id.remote_video_layout);
        remoteRender = (CircularSurfaceViewRenderer) findViewById(R.id.remote_video_view);

        rootEglBase = EglBase.create();
        remoteRender.init(rootEglBase.getEglBaseContext(), null);
        remoteVideoTrack = null;



        initWebRTC();

        socket = SocketIOManager.getInstance().mSocket;
        openBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (!peerStarted) {
                    sigConnect(getResources().getString(R.string.roomURL));
                }
            }
        });

        muteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (peerStarted) {
                    if (localAudioTrack.enabled()) {
                        localAudioTrack.setEnabled(false);
                        muteBtn.setImageResource(R.drawable.unmute);
                    } else {
                        localAudioTrack.setEnabled(true);
                        muteBtn.setImageResource(R.drawable.mute);
                    }
                }
            }
        });

        dropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (peerStarted) {
                    hangUp();
                }
                Intent intent = new Intent(getApplicationContext(),RoomActivity.class);
                startActivity(intent);
            }
        });

        updateVideoView();
    }

    private void initWebRTC() {
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
        peerConnectionFactory = new PeerConnectionFactory(null);
        pcConstraints = new MediaConstraints();
        videoConstraints = new MediaConstraints();
        audioConstraints = new MediaConstraints();
        mediaConstraints = new MediaConstraints();
        final MediaConstraints.KeyValuePair audioPair = new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true");
        final MediaConstraints.KeyValuePair videoPair = new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true");
        mediaConstraints.mandatory.add(audioPair);
        mediaConstraints.mandatory.add(videoPair);
        mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        AudioSource mAudioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, mAudioSource);
        mediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);
        mediaStream.addTrack(localAudioTrack);
    }

    private void updateVideoView() {
        remoteRenderLayout.setPosition(0, 0, 100, 100);
        remoteRender.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        remoteRender.setMirror(false);
        remoteRender.requestLayout();
    }

    private void connect() {
        if (peerStarted == false) {
            sendOffer();
            peerStarted = true;
        }
    }

    private void hangUp() {
        sendDisconnect();
        stop();
        peerStarted = false;
    }

    private void stop() {
        if (peerConnection != null) {
            peerConnection.close();
            peerConnection = null;
            peerStarted = false;
        }
    }

    private PeerConnection prepareNewConnection() {
        LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<PeerConnection.IceServer>();
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
        peerConnection = peerConnectionFactory.createPeerConnection(iceServers, pcConstraints, (PeerConnection.Observer) context);
        peerConnection.addStream(mediaStream);
        return peerConnection;
    }

    private void onOffer(SessionDescription sdp) {
        setOffer(sdp);
        sendAnswer();
        peerStarted = true;
    }

    private void onAnswer(SessionDescription sdp) {
        setAnswer(sdp);
    }

    private void onCandidate(IceCandidate candidate) {
        peerConnection.addIceCandidate(candidate);
    }

    private void sendSDP(SessionDescription sdp) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", sdp.type.canonicalForm());
            json.put("sdp", sdp.description);
            sigSend(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendOffer() {
        peerConnection = prepareNewConnection();
        peerConnection.createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                peerConnection.setLocalDescription(this, sessionDescription);
                sendSDP(sessionDescription);
            }

            @Override
            public void onSetSuccess() {

            }

            @Override
            public void onCreateFailure(String s) {

            }

            @Override
            public void onSetFailure(String s) {

            }
        }, mediaConstraints);
    }

    private void setOffer(SessionDescription sdp) {
        if (peerConnection != null) {
            System.out.print("peer connection already exists");
        }
        peerConnection = prepareNewConnection();
        peerConnection.setRemoteDescription(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {

            }

            @Override
            public void onSetSuccess() {

            }

            @Override
            public void onCreateFailure(String s) {

            }

            @Override
            public void onSetFailure(String s) {

            }
        }, sdp);
    }

    private void sendAnswer() {
        System.out.print("Sending Answer. Creating remote session description...");
        if (peerConnection == null){
            System.out.print("peerConnection Not Exist");
            return;
        }
        peerConnection.createAnswer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                peerConnection.setLocalDescription(this, sessionDescription);
                sendSDP(sessionDescription);
            }

            @Override
            public void onSetSuccess() {

            }

            @Override
            public void onCreateFailure(String s) {

            }

            @Override
            public void onSetFailure(String s) {

            }
        }, mediaConstraints);
    }

    private void setAnswer(SessionDescription sdp) {
        if (peerConnection == null) {
            System.out.print("peerConnection Not Exit");
            return;
        }
        peerConnection.setRemoteDescription(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                peerConnection.setLocalDescription(this, sessionDescription);
                sendSDP(sessionDescription);
            }

            @Override
            public void onSetSuccess() {

            }

            @Override
            public void onCreateFailure(String s) {

            }

            @Override
            public void onSetFailure(String s) {

            }
        }, sdp);
    }

    private void sendDisconnect() {
        JSONObject json = new JSONObject();
        try {
            json.put("type", "user disconnected");
            sigSend(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sigConnect(String wsUrl) {
        wsServerUrl = wsUrl;
        System.out.print("Connecting to " + wsServerUrl);

        socket.on("connect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.print("WebSocket connection open to" + wsServerUrl);
                sigEnter();
            }
        });

        socket.on("disconnect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.print("WebSocket connection closed");
            }
        });

        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (args.length == 0)
                    return;
                JSONObject data = (JSONObject) args[0];
                System.out.print("WebServiceRespose -> C:" + data.toString());
                String type = "";
                try {
                    type = data.getString("event");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (type == "") {
                    try {
                        type = data.getString("type");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                System.out.print(type);
                if (type.equals("offer")) {
                    try {
                        SessionDescription sdp = new SessionDescription(SessionDescription.Type.valueOf(type.toUpperCase()), data.getString("sdp"));
                        onOffer(sdp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (type.equals("answer") && peerStarted) {
                    try {
                        SessionDescription sdp = new SessionDescription(SessionDescription.Type.valueOf(type.toUpperCase()), data.getString("sdp"));
                        onAnswer(sdp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (type.equals("answer")) {
                    try {
                        SessionDescription sdp = new SessionDescription(SessionDescription.Type.valueOf(type.toUpperCase()), data.getString("sdp"));
                        onAnswer(sdp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (type.equals("candidate") && peerStarted) {
                    try {
                        String remoteCandidateString = data.getString("candidate");
                        JSONObject remoteCandidate = new JSONObject(remoteCandidateString);
                        IceCandidate candidate = new IceCandidate(remoteCandidate.getString("sdpMid"),
                                remoteCandidate.getInt("sdpMLineIndex"),
                                remoteCandidate.getString("candidate")
                        );
                        onCandidate(candidate);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if ((type.equals("user disconnected") || type.equals("remote left")) && peerStarted) {
                    stop();
                } else if (type.equals("joined session")) {
                    sendOffer();
                    peerStarted = true;
                } else {

                }
            }
        });

        socket.connect();
    }

    private void sigEnter() {
        JSONObject message = new JSONObject();
        try {
            message.put("method", "createOrJoin");
            message.put("sessionId", roomID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SocketIOManager.getInstance().sendMessage(message);
    }

    private void sigSend(JSONObject message) {
        socket.emit("message", message);
    }

    @Override
    public void call(Object... args) {

    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {

    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        String stateString = "";
        switch (iceConnectionState) {
            case NEW:
                stateString = "IceConnectionNew";
                break;
            case CHECKING:
                stateString = "IceConnectionChecking";
                break;
            case CONNECTED:
                stateString = "IceConnectionConnected";
//                updateVideoView();
                break;
            case COMPLETED:
                stateString = "IcecConnectionCompleted";
                break;
            case FAILED:
                stateString = "IceConnectionFailed";
                break;
            case DISCONNECTED:
                stateString = "IceConnectionDisConnected";
                break;
            case CLOSED:
                stateString = "IceConnectionClosed";
                break;
            default:
                stateString = "Unknown";
                break;
        }
        Log.d("ICE Connection:    ", stateString);
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        System.out.println(b);
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        if (iceCandidate != null) {
            Log.d("iceCandidate:   ", iceCandidate.toString());
        }
        JSONObject json = new JSONObject();
        try {
            json.put("type", "candidate");

            JSONObject candidateJson = new JSONObject();
            candidateJson.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
            candidateJson.put("sdpMid", iceCandidate.sdpMid);
            candidateJson.put("candidate", iceCandidate.sdp);
            json.put("candidate", candidateJson);

            sigSend(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] candidates) {

    }

    @Override
    public void onAddStream(final MediaStream mediaStream) {
        if (peerConnection == null) {
            return;
        }
        if (mediaStream.audioTracks.size() > 1 || mediaStream.videoTracks.size() > 1) {
            return;
        }
        if (mediaStream.videoTracks.size() == 1) {
            remoteVideoTrack = mediaStream.videoTracks.getFirst();
            remoteVideoTrack.setEnabled(true);
            remoteVideoTrack.addRenderer(new VideoRenderer(remoteRender));
        }
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        remoteVideoTrack = null;
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {

    }

    @Override
    public void onRenegotiationNeeded() {

    }

}

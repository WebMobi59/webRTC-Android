package mmstart0312.com.webrtc_android.classes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;

import org.webrtc.SurfaceViewRenderer;

public class CircularSurfaceViewRenderer extends SurfaceViewRenderer {

    private Context context;
    private Path clipPath;

    public CircularSurfaceViewRenderer(Context context) {
        super(context);
        this.context = context;
//        init();
    }

    public CircularSurfaceViewRenderer(Context context, AttributeSet attrs) {
        super(context, attrs);

//        init();
    }

    public void drawCircleCenter(float btnLayoutHeight) {
        clipPath = new Path();
        //TODO: define the circle you actually want
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;

        Log.d("btnLayoutHeight", "" + btnLayoutHeight);

        clipPath.addCircle(width/2,  (height - btnLayoutHeight - 100) / 2 , width / 2 - 15, Path.Direction.CW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipPath(clipPath);
        super.dispatchDraw(canvas);
    }
}

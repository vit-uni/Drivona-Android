package com.hjq.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.hjq.widget.R;

public class CustomDashedLineView extends View {

    private Paint paint;
    private Path path;
    private RectF rectF;

    private int dashColor = Color.BLACK;
    private int startColor = Color.RED;
    private int endColor = Color.BLUE;
    private float dashLength = 10f;
    private float spaceLength = 10f;
    private float dashGap = 0f;
    private float strokeWidth = 2f; // 新增虚线的宽度属性
    private boolean isVertical = true;
    private boolean isRectMode = false;
    private float cornerRadius = 0f;

    public CustomDashedLineView(Context context) {
        super(context);
        init(null);
    }

    public CustomDashedLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomDashedLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        path = new Path();
        rectF = new RectF();

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomDashedLineView);
            dashColor = a.getColor(R.styleable.CustomDashedLineView_dashColor, Color.BLACK);
            startColor = a.getColor(R.styleable.CustomDashedLineView_startColor, Color.RED);
            endColor = a.getColor(R.styleable.CustomDashedLineView_endColor, Color.BLUE);
            dashLength = a.getDimension(R.styleable.CustomDashedLineView_dashLength, 10f);
            spaceLength = a.getDimension(R.styleable.CustomDashedLineView_spaceLength, 10f);
            dashGap = a.getDimension(R.styleable.CustomDashedLineView_dashGap, 0f);
            strokeWidth = a.getDimension(R.styleable.CustomDashedLineView_strokeWidth, 2f); // 读取虚线的宽度属性
            isVertical = a.getBoolean(R.styleable.CustomDashedLineView_isVertical, true);
            isRectMode = a.getBoolean(R.styleable.CustomDashedLineView_isRectMode, false);
            cornerRadius = a.getDimension(R.styleable.CustomDashedLineView_cornerRadius, 0f);
            a.recycle();
        }
    }

    public void setDashColor(int color) {
        dashColor = color;
        invalidate();
    }

    public void setGradientColors(int startColor, int endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
        invalidate();
    }

    public void setDashLength(float dashLength) {
        this.dashLength = dashLength;
        invalidate();
    }

    public void setSpaceLength(float spaceLength) {
        this.spaceLength = spaceLength;
        invalidate();
    }

    public void setDashGap(float dashGap) {
        this.dashGap = dashGap;
        invalidate();
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        invalidate();
    }

    public void setIsVertical(boolean isVertical) {
        this.isVertical = isVertical;
        invalidate();
    }

    public void setIsRectMode(boolean isRectMode) {
        this.isRectMode = isRectMode;
        invalidate();
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // 渐变色
        Shader shader = new LinearGradient(0, 0, width, 0, startColor, endColor, Shader.TileMode.CLAMP);
        paint.setShader(shader);

        // 设置虚线效果
        paint.setColor(dashColor);
        paint.setStrokeWidth(strokeWidth); // 设置虚线的宽度
        if (dashGap > 0f) {
            // 设置虚线间距
            paint.setPathEffect(new DashPathEffect(new float[]{dashLength, spaceLength, dashGap}, 0));
        } else {
            paint.setPathEffect(new DashPathEffect(new float[]{dashLength, spaceLength}, 0));
        }

        // 绘制虚线或虚线矩形
        if (isRectMode) {
            drawDashedRect(canvas, width, height);
        } else {
            drawDashedLine(canvas, width, height);
        }
    }

    private void drawDashedLine(Canvas canvas, int width, int height) {
        path.reset();

        if (isVertical) {
            float x = width / 2f;
            path.moveTo(x, 0);
            path.lineTo(x, height);
        } else {
            float y = height / 2f;
            path.moveTo(0, y);
            path.lineTo(width, y);
        }

        canvas.drawPath(path, paint);
    }

    private void drawDashedRect(Canvas canvas, int width, int height) {
        rectF.set(0, 0, width, height);

        if (cornerRadius > 0) {
            float x = width / 2f;
            float y = height / 2f;

            // 使用Path构建带有圆角的矩形
            path.reset();
            path.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW);
            canvas.drawPath(path, paint);
        } else {
            canvas.drawRect(rectF, paint);
        }
    }
}

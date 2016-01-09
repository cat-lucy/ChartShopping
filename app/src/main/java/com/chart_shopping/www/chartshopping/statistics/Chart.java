package com.chart_shopping.www.chartshopping.statistics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.chart_shopping.www.chartshopping.categories.CostCategory;

public class Chart extends View {

    private Paint paint = new Paint();
    float[] percentage;
    int selected_category;

    public Chart(Context context, AttributeSet attribute_set) {
        super(context, attribute_set);
        this.percentage = null;
    }

    public void setValue (float[] percentage) {
        this.percentage = percentage;
        this.selected_category = 0;
    }

    public void selectCategory (int selected_category) {
        this.selected_category = selected_category;
        invalidate();
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        int diameter = Math.min(getWidth(), getHeight());
        int indent = diameter / 35;
        int d_indent = indent * 2;

        RectF rect = new RectF(d_indent, d_indent, diameter - d_indent, diameter - d_indent);
        RectF selected_rect = new RectF(indent, indent, diameter - indent, diameter - indent);

        if (percentage != null) {
            float alpha = 0;
            for (int i = 0; i < percentage.length; i++) {
                paint.setColor(CostCategory.colors[i]);
                if (i != selected_category)
                    canvas.drawArc(rect, alpha, 360 * percentage[i], true, paint);
                else
                    canvas.drawArc(selected_rect, alpha, 360 * percentage[i], true, paint);
                alpha += 360 * percentage[i];
            }
        } else {
            paint.setARGB(0, 255, 255, 255);
            canvas.drawArc(rect, 0, 360, true, paint);
        }
    }
}

package com.touguyun.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.touguyun.R;
import com.touguyun.module.TouguJsonObject;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by zhengyonghui on 15/8/29.
 *
 * 大盘指数应该与组合原点（起始点）相同，如不相同，最高线和最低线采用多个组合最大和最小点
 *
 */
public class LineChartView extends View{
    public LineChartView(Context context) {
        super(context);
        initView(context);
    }
    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private List<String> dateList;//日期
    private List<LineChart> lines;//原始股指
    private List<xLine> xLines;     //X轴
    public int maxData=0;
    public int minData=0;
    public int originData;
    private float dp = 1f;

    public void setData(List<String> date){
        this.dateList = date;
    }
    public void addLine(int color,String name,List<Float> datas){
        if(lines == null){
            lines = new ArrayList<LineChart>();
        }
        List<Integer> lineDatas = new ArrayList<>();
        for (Float data:datas){
            lineDatas.add((int)(data*10000));
        }
        lines.add(new LineChart(color,name,lineDatas));
        this.postInvalidate();
    }

    public void initView(Context context){
        dp = getResources().getDisplayMetrics().density;
        this.setBackgroundColor(Color.TRANSPARENT);
//        setData(TouguJsonObject.parseList("[\"2015-08-16\",\"2015-08-16\",\"2015-08-16\"]",String.class));
//        addLine(getContext().getResources().getColor(R.color.red_FB3636), "ZH171885-4.36%", TouguJsonObject.parseList("[234,333,345]", Integer.class));
//        addLine(getContext().getResources().getColor(R.color.blue_15A1FF),"沪深300 -4.36%",TouguJsonObject.parseList("[234,343,232]",Integer.class));
    }
    /**
     * 原始股指数据
     */
    private class LineChart{
        public int color;
        public String name;
        public List<Integer> datas;
        public List<Point> points;
        public LineChart(int color,String name,List<Integer> datas){
            this.color = color;
            this.name = name;
            this.datas = datas;
            if(datas!=null){
//                originData = datas.get(0).intValue();
                minData = minData==0?datas.get(0).intValue():minData;
                for (Integer data:datas){
                    maxData = data>maxData?data.intValue():maxData;
                    minData = data<minData?data.intValue():minData;
                }
            }
        }
        public void initPoints(float f,int dataBottom,int width){
            points = new ArrayList<Point>();
            if(this.datas!=null){
                for (int i=0;i<datas.size();i++){
                    Point point = new Point();
                    point.set((width/(datas.size()-1))*i,(int)((datas.get(i)-dataBottom)/f));
                    points.add(point);
                }
            }
        }
    }
    /**
     * x轴及刻度
     */
    private class xLine{
        public int yHeight;
        public float percen;
        public String percentName;//振幅百分比
    }
    /**
     * 计算出x轴相关信息
     */
    private void initXLine(){
        if(originData == 0){
            originData=10000;
        }
        float maxPercent = new BigDecimal((float)(maxData - originData)/(float)originData).setScale(4,BigDecimal.ROUND_HALF_UP).floatValue();
        float minPercent = new BigDecimal((float)(minData - originData)/(float)originData).setScale(4,BigDecimal.ROUND_HALF_UP).floatValue();
        xLines = new ArrayList<xLine>();
        if(maxPercent<0.05f && maxPercent>0){
            maxPercent = 0.05f;
        }
        if(minPercent<0.05f && minPercent>0){
            minPercent = 0.05f;
        }
        if(maxPercent == 0f && minPercent<0f){
            xLines.add(getXLine(0f,0,3));
            xLines.add(getXLine(getXpercent(minPercent)/2,1,3));
            xLines.add(getXLine(getXpercent(minPercent),2,3));
        }else if(maxPercent>0f && minPercent == 0){
            xLines.add(getXLine(getXpercent(maxPercent),0,3));
            xLines.add(getXLine(getXpercent(maxPercent/2),1,3));
            xLines.add(getXLine(0f,2,3));
        }else if(Math.abs(getXpercent(maxPercent)) == Math.abs(getXpercent(minPercent))){
            if(Math.abs(getXpercent(maxPercent))>=0.2f){
                xLines.add(getXLine(getXpercent(maxPercent),0,5));
                xLines.add(getXLine(getXpercent(maxPercent/2),1,5));
                xLines.add(getXLine(0f,2,5));
                xLines.add(getXLine(getXpercent(minPercent)/2,3,5));
                xLines.add(getXLine(getXpercent(minPercent),4,5));
            }else{
                xLines.add(getXLine(getXpercent(maxPercent),0,3));
                xLines.add(getXLine(0f,1,3));
                xLines.add(getXLine(getXpercent(minPercent),2,3));
            }
        }else if(Math.abs(getXpercent(maxPercent)) > Math.abs(getXpercent(minPercent))){
            xLines.add(getXLine(getXpercent(maxPercent),0,4));
            xLines.add(getXLine(getXpercent(maxPercent/2),1,4));
            xLines.add(getXLine(0f,2,4));
            xLines.add(getXLine(-getXpercent(maxPercent/2),3,4));
        }else{
            xLines.add(getXLine(-getXpercent(minPercent/2),0,4));
            xLines.add(getXLine(0f,1,4));
            xLines.add(getXLine(getXpercent(minPercent)/2,2,4));
            xLines.add(getXLine(getXpercent(minPercent),3,4));
        }
    }
    private xLine getXLine(float percen,int index,int num){
        xLine line = new xLine();
        line.percen = percen;
        if(index == 0){
           line.yHeight = 45;
        }else{
            line.yHeight = ((getHeight()-90)/(num-1))*index+45;
        }
        line.percentName = (percen<0?"-":"")+percentNameMap.get(Math.abs(percen));
        return line;
    }
    private float getXpercent(float num){
        float temp = Math.abs(num);
        if(temp == 0){
            return 0f;
        }else if(temp<0.05f){
            return num>0?0.05f:-0.05f;
        }else if(temp<0.1f){
            return num>0?0.1f:-0.1f;
        }else if(temp<0.2f){
            return num>0?0.2f:-0.2f;
        }else if(temp<0.5f){
            return num>0?0.5f:-0.5f;
        }else if(temp<1.0f){
            return num>0?1.0f:-1.0f;
        }else if(temp<1.5f){
            return num>0?1.5f:-1.5f;
        }else if(temp<2.0f){
            return num>0?2.0f:-2.0f;
        }else if(temp<3.0f){
            return num>0?3.0f:-3.0f;
        }else if(temp<5.0f){
            return num>0?5.0f:-5.0f;
        }else if(temp<10.0f){
            return num>0?10.0f:-10.0f;
        }else if(temp<20.0f){
            return num>0?20.0f:-20.0f;
        }else if(temp<50.0f){
            return num>0?50.0f:-50.0f;
        }else{
            return num>0?100.0f:-100.0f;
        }
    }

    private static final Map<Float,String> percentNameMap = new HashMap<Float,String>() {
        {
            put(0f,"0%");
            put(0.05f,"5%");
            put(0.1f,"10%");
            put(0.2f,"20%");
            put(0.25f,"25%");
            put(0.5f,"50%");
            put(0.75f,"75%");
            put(1.0f,"100%");
            put(1.5f,"150%");
            put(2.0f,"200%");
            put(2.5f,"250%");
            put(3.0f,"300%");
            put(5.0f,"500%");
            put(10.0f,"1000%");
            put(20.0f,"2000%");
            put(25.0f,"2500%");
            put(50.0f,"5000%");
            put(100.0f,"10000%");
        }
    };
    private int getMaxOrMinData(float f){
        return Math.round(originData * f) + originData;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true); //去锯齿
        paint.setColor(getResources().getColor(R.color.gray_D8D8D8));

        Paint paintText = new Paint();
        paintText.setTextSize(9 * dp);
        paint.setAntiAlias(true); //去锯齿
        paintText.setColor(getResources().getColor(R.color.gray_858585));
        paintText.setTextAlign(Paint.Align.RIGHT);

        int dataBottom = 0;
        float f = 0;
        //画Y轴
        canvas.drawLine(getWidth()-1, 0, getWidth()-1, getHeight(), paint);
        //画X轴
        initXLine();
        if(xLines!=null){
            for (xLine line:xLines){
                canvas.drawLine(0, line.yHeight, getWidth(), line.yHeight, paint);
                canvas.drawText(line.percentName,getWidth()-(3*dp),line.yHeight-(2*dp),paintText);
            }
            dataBottom = getMaxOrMinData(xLines.get(xLines.size()-1).percen);
            f = ((float)(getMaxOrMinData(xLines.get(0).percen)-dataBottom))/(float)(xLines.get(xLines.size()-1).yHeight - xLines.get(0).yHeight);
        }
        //画日期
        if(dateList!=null){
            Paint paintDate = new Paint();
            paintDate.setTextSize(9 * dp);
            paintDate.setColor(getResources().getColor(R.color.gray_858585));
            paint.setAntiAlias(true); //去锯齿
            paintDate.setTextAlign(Paint.Align.LEFT);
            if(dateList.size() == 3){
                canvas.drawText(dateList.get(0),0,getHeight()-2*dp,paintDate);
                canvas.drawText(dateList.get(1),getWidth()/2-(24*dp),getHeight()-2*dp,paintDate);
                canvas.drawText(dateList.get(2),getWidth()-(48*dp),getHeight()-2*dp,paintDate);
            }else{
                for (int i=0;i<dateList.size();i++){
                    canvas.drawText(dateList.get(i),getWidth()/dateList.size()*i+(47*dp/dateList.size()),getHeight()-2*dp,paintDate);
                }
            }
        }


        //画折线
        if(lines!=null){
            float msgWidth = 0;
            for (LineChart line:lines){
                if(line!=null){
                    Paint paintMsg = new Paint();
                    paintMsg.setAntiAlias(true);
                    paintMsg.setColor(getResources().getColor(R.color.gray_858585));
                    paintMsg.setTextSize(9.3f*dp);
                    paintMsg.setTextAlign(Paint.Align.LEFT);

                    Paint paintLine = new Paint();
                    paintLine.setStyle(Paint.Style.FILL);
                    paintLine.setAntiAlias(true); //去锯齿
                    paintLine.setColor(line.color);
                    paintLine.setStrokeWidth(1*dp);

                    //画折线信息
                    canvas.drawRect(msgWidth,5*dp,msgWidth+13.3f*dp,9*dp,paintLine);
                    msgWidth =  msgWidth+15*dp;
                    canvas.drawText(line.name,msgWidth,10*dp,paintMsg);
                    msgWidth += getTextWidth(paintMsg,line.name) + 6*dp;

                    //画折线
                    line.initPoints(f,dataBottom,getWidth());
                    if(line.points!=null && xLines!=null){
                        for (int i=1;i<line.points.size();i++){
                            canvas.drawLine(line.points.get(i-1).x, xLines.get(xLines.size()-1).yHeight-line.points.get(i-1).y, line.points.get(i).x, xLines.get(xLines.size()-1).yHeight-line.points.get(i).y, paintLine);
                        }
                    }

                }
            }
        }
    }
    public static int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }
}

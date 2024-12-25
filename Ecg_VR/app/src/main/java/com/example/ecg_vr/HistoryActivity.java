package com.example.ecg_vr;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends Activity {
    private BarChart bar;

    List<BarEntry> list=new ArrayList<>();//实例化一个List用来存储数据
    List<PieEntry> plist = new ArrayList<>();
    float[] data_bar = new float[]{0,0,0,0,0,0,0};
    // 实例化控件
    private Button dateButton;
    private Button timeButton;
    private EditText editText;
    private TextView date_show,data1,data2,data3,data4,data5;
    private DatePickerDialog dateDialog;
    private TimePickerDialog timeDialog;
    private BarChart mBarChart;
    private PieChart pie;
    private Legend legend;
    private int year, monthOfYear, dayOfMonth, hourOfDay, minute ;
    private String account;
    private float todayH = (float) 1.2;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏

        Intent intent=getIntent();
        account=intent.getStringExtra("account");

        // 通过findViewById找到控件
        //dateButton = (Button) findViewById(R.id.button1);
        date_show = findViewById(R.id.date_show);
        data1 = findViewById(R.id.data1);
        data2 = findViewById(R.id.data2);
        data3 = findViewById(R.id.data3);
        data4 = findViewById(R.id.data4);
        data5 = findViewById(R.id.data5);
        //timeButton = (Button) findViewById(R.id.button2);
        //editText = (EditText) findViewById(R.id.edit);
        // 通过Calendar对象来获取年、月、日、时、分的信息
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(calendar.YEAR);
        monthOfYear = calendar.get(calendar.MONTH)+1;
        dayOfMonth = calendar.get(calendar.DAY_OF_MONTH);
        hourOfDay = calendar.get(calendar.HOUR_OF_DAY);
        minute = calendar.get(calendar.MINUTE);

        bar = (BarChart) findViewById(R.id.bar);
        //添加数据

        float[] data_init = new float[]{(float) 4.1,(float) 3.8,(float) 6.2,6,(float) 1.1,(float)1.9,todayH};
        int Second=0,midday,midmonth;
        for(int i=0;i<7;i++){
            Second =0;
            midday = dayOfMonth - 6 + i;
            midmonth = monthOfYear;

            if(midday<1){
                midday = getMonthLastDay(year,monthOfYear) + midday;
                midmonth = (midmonth +10)%12 +1;
            }
            SharedPreferences spf1 = getSharedPreferences(account+"-"+year+"-"+midmonth+"-"+midday,MODE_PRIVATE);
            Log.d("data", "load : " + account + "-" + year + "-" + monthOfYear + "-" + dayOfMonth);

            if(spf1.getBoolean("isExist",false)){
                Second += spf1.getInt("scared_time", 0);
                Second += spf1.getInt("sick_time", 0);
                Second += spf1.getInt("neutral_time", 0);
                Second += spf1.getInt("positive_time", 0);
                list.add(new BarEntry(i, ((float)Second/3600)));
                data_bar[i] = Second;
            }else{
                list.add(new BarEntry(i, data_init[i]));
                data_bar[i] = data_init[i]*3600;
            }



        }

        barInit();

        pie = findViewById(R.id.pie);

        pieInit();



//        /**
//         * 对日期选择器按钮设置监听事件
//         */
//        dateButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                //设置起始日期和结束日期
//                DatePicker datePicker = dateDialog.getDatePicker();
//                datePicker.setMinDate(System.currentTimeMillis() - 60*60*24*14*1000);
//                datePicker.setMaxDate(System.currentTimeMillis());
//                dateDialog.show();
//            }
//        });
//
//        /*
//         * 实例化TimePickerDialog
//         */
//        timeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
//
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                // TODO Auto-generated method stub
//                Toast.makeText(HistoryActivity.this, hourOfDay + ":" + minute,
//                        Toast.LENGTH_LONG).show();
//            }
//        }, hourOfDay, minute, true); // 最后一个参数设置是否为24小时制
//        /**
//         * 对时间选择器按钮设置监听事件
//         */
//        timeButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // 点击时间选择器按钮时显示出时间对话框
//                timeDialog.show();
//            }
//        });
    }

    public void dateChange(View view){
        /*
         * 实例化DatePickerDialog
         */
        dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker arg0, int year, int monthOfYear,
                                  int dayOfMonth) {
                // 把获取的日期显示在文本框内，月份从0开始计数，所以要加1
                String text = year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日" ;
                //boolean mid = fileIsExists(account+year+(monthOfYear+1)+dayOfMonth);
                //Toast.makeText(HistoryActivity.this,account+year+(monthOfYear+1)+dayOfMonth+" is "+mid,Toast.LENGTH_SHORT).show();
                //editText.setText(text);
                date_show.setText(text);
                //dateButton.setText(text);

                float rate;
                float mid;

                float rate1;
                float rate2;
                float rate3;
                float rate4;

                SharedPreferences spf1 = getSharedPreferences(account+"-"+year+"-"+(monthOfYear + 1)+"-"+dayOfMonth,MODE_PRIVATE);
                if(spf1.getBoolean("isExist",false)){
                    int sc_t = spf1.getInt("scared_time", 0);
                    int si_t = spf1.getInt("sick_time", 0);
                    int n_t = spf1.getInt("neutral_time", 0);
                    int p_t = spf1.getInt("positive_time", 0);
                    int Second = sc_t + si_t + n_t + p_t;

                    plist.clear();
                    plist.add(new PieEntry(si_t,"消极-恶心"));
                    plist.add(new PieEntry(sc_t,"消极-惊恐"));
                    plist.add(new PieEntry(p_t,"积极"));
                    plist.add(new PieEntry(n_t,"平静"));

                    data1.setText("总时间："+moreSecond(Second));
                    data2.setText("消极-恶心时间："+moreSecond(si_t));
                    data3.setText("消极-惊恐时间："+moreSecond(sc_t));
                    data4.setText("积极时间："+moreSecond(p_t));
                    data5.setText("平静时间："+moreSecond(n_t));

                    //设置中心部分的字  （一般中间白色圆不隐藏的情况下才设置）
                    pie.setCenterText("游戏时间\n"+inSecond(Second));
                }else{
                    rate = (float) Math.random();
                    mid = rate*8;

                    rate1 = (float) Math.random();
                    rate2 = (float) Math.random();
                    rate3 = (float) Math.random();
                    rate4 = (float) Math.random();

                    rate1 = rate1/(rate1+rate2+rate3+rate4);
                    rate2 = rate2/(rate1+rate2+rate3+rate4);
                    rate3 = rate3/(rate1+rate2+rate3+rate4);
                    rate4 = rate4/(rate1+rate2+rate3+rate4);

                    plist.clear();
                    plist.add(new PieEntry(mid*rate1*3600,"消极-恶心"));
                    plist.add(new PieEntry(mid*rate2*3600,"消极-惊恐"));
                    plist.add(new PieEntry(mid*rate3*3600,"积极"));
                    plist.add(new PieEntry(mid*rate4*3600,"平静"));

                    data1.setText("总时间："+(int)(mid*60)+"分钟");
                    data2.setText("消极-恶心时间："+(int)(mid*rate1*60)+"分钟");
                    data3.setText("消极-惊恐时间："+(int)(mid*rate2*60)+"分钟");
                    data4.setText("积极时间："+(int)(mid*rate3*60)+"分钟");
                    data5.setText("平静时间："+(int)(mid*rate4*60)+"分钟");

                    //设置中心部分的字  （一般中间白色圆不隐藏的情况下才设置）
                    pie.setCenterText("游戏时间\n"+String.format("%.2f", mid)+"小时");
                }

                PieDataSet pieDataSet=new PieDataSet(plist,"");
                PieData pieData=new PieData(pieDataSet);
                pie.setData(pieData);

                //设置中心字的字体大小
                pie.setCenterTextSize(16);
                //设置描述的字体大小
                pie.setEntryLabelTextSize(20);
                pie.setEntryLabelColor(Color.BLACK);
                //设置数据的字体大小
                pieDataSet.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                        if (entry.getY()==v){
                            return inSecond((int)v);
                        }
                        return "";
                    }
                });
                pieDataSet.setValueTextSize(20);
                pieDataSet.setValueTextColor(Color.parseColor("#000000"));
                //设置描述的位置
                pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                pieDataSet.setValueLinePart1Length(0.08f);//设置描述连接线长度
                //设置数据的位置
                pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                pieDataSet.setValueLinePart2Length(0.08f);//设置数据连接线长度
                //设置两根连接线的颜色
                pieDataSet.setValueLineColor(Color.WHITE);

                //对于右下角一串字母的操作
                pie.getDescription().setEnabled(false);                  //是否显示右下角描述
                pie.getDescription().setText("这是修改那串英文的方法");    //修改右下角字母的显示
                pie.getDescription().setTextSize(20);                    //字体大小
                pie.getDescription().setTextColor(Color.RED);             //字体颜色

                //图例
                Legend legend=pie.getLegend();
                legend.setEnabled(false);    //是否显示图例
                legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);    //图例的位置

                pieDataSet.setColors(Color.YELLOW,Color.RED,Color.GREEN,Color.WHITE);//设置各个数据的颜色
                pie.notifyDataSetChanged();
                pie.invalidate();
                pie.animateY(2000);


            }
        }, year, monthOfYear, dayOfMonth); // 后面的三个参数对应于上面的年、月、日

        //设置起始日期和结束日期
        DatePicker datePicker = dateDialog.getDatePicker();
        datePicker.setMinDate((long)(1682404246805L));
        Log.d("date", "dateChange: !!!!!!!!!!!!!!!!"+(System.currentTimeMillis() - 60*60*24*20*1000));
        datePicker.setMaxDate(System.currentTimeMillis());
        dateDialog.show();
    }


    /**
     * 得到指定月的天数
     * */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int getMonthLastDay(int year, int month)
    {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    public void barInit(){
        BarDataSet barDataSet=new BarDataSet(list,"时间");
        BarData barData=new BarData(barDataSet);
        bar.setData(barData);

        //图背景
        bar.setDrawBorders(false);
        bar.setBackgroundColor(0x00000000);   //背景颜色
        bar.getXAxis().setDrawGridLines(false);  //是否绘制X轴上的网格线（背景里面的竖线）
        bar.getAxisLeft().setDrawGridLines(false);  //是否绘制Y轴上的网格线（背景里面的横线）

        //对于右下角一串字母的操作
        bar.getDescription().setEnabled(false);                  //是否显示右下角描述
        bar.getDescription().setText("这是修改那串英文的方法");    //修改右下角字母的显示
        bar.getDescription().setTextSize(20);                    //字体大小
        bar.getDescription().setTextColor(Color.RED);             //字体颜色

        //图例
        legend=bar.getLegend();
        legend.setEnabled(false);    //是否显示图例

        //X轴
        XAxis xAxis=bar.getXAxis();
        //文字倾斜展示
        xAxis.setLabelRotationAngle(-45);
        xAxis.setDrawAxisLine(false);//不显示x轴线条;
        xAxis.setDrawGridLines(false);  //是否绘制X轴上的网格线（背景里面的竖线）
        xAxis.setAxisLineColor(R.color.teal_700);   //X轴颜色
        xAxis.setAxisLineWidth(2);           //X轴粗细
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);        //X轴所在位置   默认为上面
        xAxis.setValueFormatter(new IAxisValueFormatter() {   //X轴自定义坐标

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                String mid;
                int day = dayOfMonth+(int)v-6;
                int maxday = getMonthLastDay(year,monthOfYear);
                if(day>maxday){
                    mid = monthOfYear+1 + "月" + (day-maxday) + "日";
                }else {
                    mid = monthOfYear + "月" + day + "日";
                }
                return mid;

            }
        });
        //xAxis.setAxisMaximum((float) 13.5);   //X轴最大数值
        //xAxis.setAxisMinimum((float) -0.5);   //X轴最小数值
        //X轴坐标的个数    第二个参数一般填false     true表示强制设置标签数 可能会导致X轴坐标显示不全等问题
        xAxis.setLabelCount(6,false);
        //bar.setVisibleXRange(8,13);

        //Y轴
        YAxis AxisLeft=bar.getAxisLeft();
        YAxis AxisRight = bar.getAxisRight();
        AxisLeft.setDrawAxisLine(false);//不显示y轴线条
        AxisLeft.setDrawGridLines(true);  //是否绘制Y轴上的网格线（背景里面的横线）
        //右侧Y轴网格线设置为虚线
        AxisLeft.enableGridDashedLine(10f, 10f, 0f);
        AxisLeft.setAxisLineColor(R.color.teal_200);  //Y轴颜色
        AxisLeft.setAxisLineWidth(2);           //Y轴粗细
        AxisLeft.setValueFormatter(new IAxisValueFormatter() {  //Y轴自定义坐标
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {

                for (int a=0;a<8;a++){     //用个for循环方便
                    if (a==v){
                        return a+"小时";
                    }
                }

                return "";
            }
        });
        AxisLeft.setAxisMaximum(8);   //Y轴最大数值
        AxisLeft.setAxisMinimum(0);   //Y轴最小数值
        //Y轴坐标的个数    第二个参数一般填false     true表示强制设置标签数 可能会导致X轴坐标显示不全等问题
        AxisLeft.setLabelCount(8,false);

        //是否隐藏右边的Y轴（不设置的话有两条Y轴 同理可以隐藏左边的Y轴）
        bar.getAxisRight().setEnabled(false);

        //柱子
        barData.setBarWidth(0.5f);//柱子宽度
//        barDataSet.setColor(Color.BLACK);  //柱子的颜色
        barDataSet.setColors(Color.GRAY);//设置柱子多种颜色  循环使用
        barDataSet.setBarBorderColor(Color.parseColor("#122E2E"));//柱子边框颜色
        barDataSet.setBarBorderWidth(1);       //柱子边框厚度
        barDataSet.setBarShadowColor(Color.RED);
        barDataSet.setValueTextSize(10);
        barDataSet.setHighlightEnabled(true);//选中柱子是否高亮显示  默认为true
        barDataSet.setStackLabels(new String[]{"aaa","bbb","ccc"});
        //定义柱子上的数据显示    可以实现加单位    以及显示整数（默认是显示小数）
        barDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                if (entry.getY()==v){
                    return inSecond((int)(data_bar[(int)entry.getX()]));
                }
                return "";
            }
        });

        //数据更新
        bar.notifyDataSetChanged();
        bar.invalidate();

        //动画（如果使用了动画可以则省去更新数据的那一步）
        bar.animateY(3000); //在Y轴的动画  参数是动画执行时间 毫秒为单位
//        line.animateX(2000); //X轴动画
//        line.animateXY(2000,2000);//XY两轴混合动画
        bar.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                e.getX();       //X轴坐标 记得转 int
                float mid = e.getY();       //当前柱状图Y轴值
                e.getIcon();    //对应 BarEntry(float x, float y, Drawable icon)
                e.getData();    //对应 BarEntry(float x, float y, Object data)
                //Toast.makeText(HistoryActivity.this," "+mid,Toast.LENGTH_SHORT).show();

                String date;
                SharedPreferences spf1;
                int day = dayOfMonth+(int)e.getX()-6;
                int maxday = getMonthLastDay(year,monthOfYear);
                if(day>maxday){
                    date = year + "年" + monthOfYear+1 + "月" + (day-maxday) + "日";
                    spf1 = getSharedPreferences(account+"-"+year+"-"+monthOfYear+1+"-"+(day-maxday),MODE_PRIVATE);

                }else {
                    date = year + "年" + monthOfYear + "月" + day + "日";
                    spf1 = getSharedPreferences(account+"-"+year+"-"+monthOfYear+"-"+day,MODE_PRIVATE);

                }
                date_show.setText(date);

                if(spf1.getBoolean("isExist",false)){
                    int sc_t = spf1.getInt("scared_time", 0);
                    int si_t = spf1.getInt("sick_time", 0);
                    int n_t = spf1.getInt("neutral_time", 0);
                    int p_t = spf1.getInt("positive_time", 0);
                    int Second = sc_t + si_t + n_t + p_t;

                    plist.clear();
                    plist.add(new PieEntry(si_t,"消极-恶心"));
                    plist.add(new PieEntry(sc_t,"消极-惊恐"));
                    plist.add(new PieEntry(p_t,"积极"));
                    plist.add(new PieEntry(n_t,"平静"));

                    data1.setText("总时间："+moreSecond(Second));
                    data2.setText("消极-恶心时间："+moreSecond(si_t));
                    data3.setText("消极-惊恐时间："+moreSecond(sc_t));
                    data4.setText("积极时间："+moreSecond(p_t));
                    data5.setText("平静时间："+moreSecond(n_t));

                    //设置中心部分的字  （一般中间白色圆不隐藏的情况下才设置）
                    pie.setCenterText("游戏时间\n"+inSecond(Second));
                }else{
                    float rate1 = (float) Math.random();
                    float rate2 = (float) Math.random();
                    float rate3 = (float) Math.random();
                    float rate4 = (float) Math.random();
                    rate1 = rate1/(rate1+rate2+rate3+rate4);
                    rate2 = rate2/(rate1+rate2+rate3+rate4);
                    rate3 = rate3/(rate1+rate2+rate3+rate4);
                    rate4 = rate4/(rate1+rate2+rate3+rate4);


                    plist.clear();
                    plist.add(new PieEntry(mid*rate1*3600,"消极-恶心"));
                    plist.add(new PieEntry(mid*rate2*3600,"消极-惊恐"));
                    plist.add(new PieEntry(mid*rate3*3600,"积极"));
                    plist.add(new PieEntry(mid*rate4*3600,"平静"));

                    data1.setText("总时间："+(int)(mid*60)+"分钟");
                    data2.setText("消极-恶心时间："+(int)(mid*rate1*60)+"分钟");
                    data3.setText("消极-惊恐时间："+(int)(mid*rate2*60)+"分钟");
                    data4.setText("积极时间："+(int)(mid*rate3*60)+"分钟");
                    data5.setText("平静时间："+(int)(mid*rate4*60)+"分钟");

                    //设置中心部分的字  （一般中间白色圆不隐藏的情况下才设置）
                    pie.setCenterText("游戏时间\n"+mid+"小时");
                }

                PieDataSet pieDataSet=new PieDataSet(plist,"");
                PieData pieData=new PieData(pieDataSet);
                pie.setData(pieData);

                //设置中心字的字体大小
                pie.setCenterTextSize(16);
                //设置描述的字体大小
                pie.setEntryLabelTextSize(20);
                pie.setEntryLabelColor(Color.BLACK);
                //设置数据的字体大小
                pieDataSet.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                        if (entry.getY()==v){
                            return inSecond((int)v);
                        }
                        return "";
                    }
                });
                pieDataSet.setValueTextSize(20);
                pieDataSet.setValueTextColor(Color.parseColor("#000000"));
                //设置描述的位置
                pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                pieDataSet.setValueLinePart1Length(0.08f);//设置描述连接线长度
                //设置数据的位置
                pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                pieDataSet.setValueLinePart2Length(0.08f);//设置数据连接线长度
                //设置两根连接线的颜色
                pieDataSet.setValueLineColor(Color.BLACK);

                //对于右下角一串字母的操作
                pie.getDescription().setEnabled(false);                  //是否显示右下角描述
                pie.getDescription().setText("这是修改那串英文的方法");    //修改右下角字母的显示
                pie.getDescription().setTextSize(20);                    //字体大小
                pie.getDescription().setTextColor(Color.RED);             //字体颜色

                //图例
                Legend legend=pie.getLegend();
                legend.setEnabled(false);    //是否显示图例
                legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);    //图例的位置

                pieDataSet.setColors(Color.YELLOW,Color.RED,Color.GREEN,Color.WHITE);//设置各个数据的颜色
                pie.notifyDataSetChanged();
                pie.invalidate();
                pie.animateY(2000);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    public void pieInit(){
        PieDataSet pieDataSet=new PieDataSet(plist,"");
        PieData pieData=new PieData(pieDataSet);
        pie.setData(pieData);
        //设置中心部分的字  （一般中间白色圆不隐藏的情况下才设置）
        pie.setCenterText("游戏时间");
        //对于右下角一串字母的操作
        pie.getDescription().setEnabled(false);                  //是否显示右下角描述
        pie.getDescription().setText("这是修改那串英文的方法");    //修改右下角字母的显示
        pie.getDescription().setTextSize(20);                    //字体大小
        pie.getDescription().setTextColor(Color.RED);             //字体颜色

        //图例
        legend=pie.getLegend();
        legend.setEnabled(false);    //是否显示图例
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);    //图例的位置

        pieDataSet.setColors(Color.YELLOW,Color.RED,Color.GREEN,Color.WHITE);//设置各个数据的颜色

    }

    /**
     * 判断文件是否存在
     * */
    public boolean fileIsExists(String strFile) {
        try {
            File f=new File(strFile);
            if(!f.exists()) {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public String inSecond(int second){
        if(second < 60){

            return second+"秒";
        }else if(second<3600){
            float mid = (float)((float)second/60);
            return String.format("%.2f", mid)+"分钟";
        }else {
            float mid = (float)((float)second/3600);
            return String.format("%.2f", mid)+"小时";
        }
    }

    public String moreSecond(int second){
        if(second < 60){

            return second+"秒";
        }else if(second<3600){
            int mid = (int)((int)second/60);
            return mid + "分钟" + second%60 + "秒";
        }else {
            int mid1 = (int)((int)second/3600);
            int mid = (int)((int)(second%3600)/60);
            return mid1+"小时" +mid + "分钟" + second%60 + "秒";
        }
    }

}
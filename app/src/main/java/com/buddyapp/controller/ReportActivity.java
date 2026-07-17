package com.buddyapp.controller;

import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.appcompat.app.AppCompatActivity;

import com.buddyapp.R;
import com.buddyapp.model.DatabaseHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private PieChart pieChart;
    private BarChart barChart;
    private int textColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        db = new DatabaseHelper(this);
        textColor = themeColor(com.google.android.material.R.attr.colorOnSurface);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);

        setUpPieChart();
        setUpBarChart();

        renderPie(db.getGenderCounts());
        renderBar(db.getStateCounts());
    }

    private void setUpPieChart() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setNoDataText(getString(R.string.chart_no_data));
        pieChart.setNoDataTextColor(textColor);
        pieChart.setEntryLabelColor(textColor);
        pieChart.getLegend().setTextColor(textColor);
        pieChart.setUsePercentValues(false);
    }

    private void setUpBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setNoDataText(getString(R.string.chart_no_data));
        barChart.setNoDataTextColor(textColor);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setTextColor(textColor);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setGranularity(1f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(textColor);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-40f);
    }

    private void renderPie(Map<String, Integer> counts) {
        if (counts.isEmpty()) {
            pieChart.clear();
            return;
        }
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextColor(textColor);
        data.setValueTextSize(14f);

        pieChart.setData(data);
        pieChart.animateY(600);
        pieChart.invalidate();
    }

    private void renderBar(Map<String, Integer> counts) {
        if (counts.isEmpty()) {
            barChart.clear();
            return;
        }
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }
        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(textColor);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setLabelCount(labels.size());
        barChart.setData(data);
        barChart.setFitBars(true);
        barChart.animateY(600);
        barChart.invalidate();
    }

    private int themeColor(@AttrRes int attr) {
        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(attr, value, true);
        return value.data;
    }
}

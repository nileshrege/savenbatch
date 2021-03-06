package com.saven.dailyalert.batch;

import com.saven.dailyalert.domain.Column;
import com.saven.dailyalert.domain.Row;
import com.saven.dailyalert.domain.XYSeriesConfig;
import com.saven.dailyalert.util.AwsS3FileUploader;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.batch.item.ItemWriter;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class LineChartWriter implements ItemWriter {

    AwsS3FileUploader fileUploader;
    Logger logger = Logger.getLogger(LineChartWriter.class.getName());

    String directory;
    String chartSaveAs;
    String label;
    String domain;
    String range;
    Integer height;
    Integer width;

    String prefix;
    String extension;
    boolean skipUpload;


    Map<String, XYSeriesConfig> seriesConfigMap;

    Map<String, XYSeries> xySeriesMap = new HashMap<>();

    @Override
    public void write(List list) throws Exception {
        list.stream().forEach(r -> populateSeries((Row) r));
        XYSeriesCollection dataSet = new XYSeriesCollection();
        xySeriesMap.entrySet().forEach(e -> dataSet.addSeries(e.getValue()));
        JFreeChart chart = createChart(dataSet);
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(ChartUtilities.encodeAsPNG(chart.createBufferedImage(width, height)));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy"); //-hh:mma
            String dateTime = LocalDateTime.now().format(formatter);

            if (!skipUpload) {
                fileUploader.upload(is, prefix + dateTime +"."+ extension);
            }
        }finally {
            if(is != null) is.close();
        }

    }

    private void populateSeries(Row row) {
        Column identityColumn = row.getColumn(row.getRowIdColumn()).get();
        XYSeriesConfig config = seriesConfigMap.get(identityColumn.getValue());

        if (!xySeriesMap.containsKey(identityColumn.getValue())) {
            String seriesName = config.getName();
            xySeriesMap.put(identityColumn.getValue(), new XYSeries(seriesName));
        }

        XYSeries xySeries = xySeriesMap.get(identityColumn.getValue());

        double xData = config.getXColumnValueMapper().map(row.getColumn(config.getXColumn()).get().getValue());
        double yData = config.getYColumnValueMapper().map(row.getColumn(config.getYColumn()).get().getValue());

        xySeries.add(xData, yData);
    }

    private double getData(Row row, String colName) {
        return Double.valueOf(row.getColumn(colName).get().getValue());
    }

    private JFreeChart createChart(XYDataset dataSet) {
        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart(
                getChartSaveAs(), // chart title
                getDomain(), // domain axis label
                getRange(), // range axis label
                dataSet,  // initial series
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
        );

        // set chart background
        chart.setBackgroundPaint(Color.white);

        // set a few custom plot features
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(0xffffe0));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);

        // set the plot's axes to display integers
        TickUnitSource ticks = NumberAxis.createIntegerTickUnits();
        NumberAxis domain = (NumberAxis) plot.getDomainAxis();
        domain.setStandardTickUnits(ticks);
        NumberAxis range = (NumberAxis) plot.getRangeAxis();
        range.setStandardTickUnits(ticks);

        // render shapes and lines
        XYLineAndShapeRenderer renderer =
                new XYLineAndShapeRenderer(true, true);
        plot.setRenderer(renderer);
        renderer.setBaseShapesVisible(true);
        renderer.setBaseShapesFilled(true);

        // set the renderer's stroke
        Stroke stroke = new BasicStroke(
                3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        renderer.setBaseOutlineStroke(stroke);

        // label the points
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        XYItemLabelGenerator generator =
                new StandardXYItemLabelGenerator(
                        StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT,
                        format, format);
        renderer.setBaseItemLabelGenerator(generator);
        renderer.setBaseItemLabelsVisible(true);

        return chart;
    }

    public Map<String, XYSeriesConfig> getSeriesConfigMap() {
        return seriesConfigMap;
    }

    public void setSeriesConfigMap(Map<String, XYSeriesConfig> seriesConfigMap) {
        this.seriesConfigMap = seriesConfigMap;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getChartSaveAs() {
        return chartSaveAs;
    }

    public void setChartSaveAs(String chartSaveAs) {
        this.chartSaveAs = chartSaveAs;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public AwsS3FileUploader getFileUploader() {
        return fileUploader;
    }

    public void setFileUploader(AwsS3FileUploader fileUploader) {
        this.fileUploader = fileUploader;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean isSkipUpload() {
        return skipUpload;
    }

    public void setSkipUpload(boolean skipUpload) {
        this.skipUpload = skipUpload;
    }
}

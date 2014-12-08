import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a bar chart.
 *
 */
public class BarChart extends ApplicationFrame {

    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
	private HashMap<String, HashMap<String, Long>> results;
	
    public BarChart(final String title, HashMap<String, HashMap<String, Long>> results) {

        super(title);
        this.results = results;
        final CategoryDataset dataset = createDataset(results);
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);

    }
    
    public BarChart(final String title) {
        super(title);
        final CategoryDataset dataset = createDatasetTest();
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);

    }

    public void display() {
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
    }

    /**
     * Returns a sample dataset.
     * 
     * @return The dataset.
     */
    private CategoryDataset createDataset(HashMap<String, HashMap<String, Long>> results) {
    	final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // take in a query type, then plot histogram for each one
    	for (Map.Entry<String, HashMap<String, Long>> entry : results.entrySet()) {
    		for (Map.Entry<String, Long> entry_inner : entry.getValue().entrySet()) {
    			 dataset.addValue(entry_inner.getValue(), entry_inner.getKey(), entry.getKey());
    		}
    	}
        return dataset;
        
    }
    
    private CategoryDataset createDatasetTest(){
    	// row keys...
        final String series1 = "First";
        final String series2 = "Second";
        final String series3 = "Third";

        // column keys...
        final String category1 = "Category 1";
        final String category2 = "Category 2";
        final String category3 = "Category 3";
        final String category4 = "Category 4";
        final String category5 = "Category 5";

        // create the dataset...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(1.0, series1, category1);
        dataset.addValue(4.0, series1, category2);
        dataset.addValue(3.0, series1, category3);
        dataset.addValue(5.0, series1, category4);
        dataset.addValue(5.0, series1, category5);

        dataset.addValue(5.0, series2, category1);
        dataset.addValue(7.0, series2, category2);
        dataset.addValue(6.0, series2, category3);
        dataset.addValue(8.0, series2, category4);
        dataset.addValue(4.0, series2, category5);

        dataset.addValue(4.0, series3, category1);
        dataset.addValue(3.0, series3, category2);
        dataset.addValue(2.0, series3, category3);
        dataset.addValue(3.0, series3, category4);
        dataset.addValue(6.0, series3, category5);
        
        return dataset;
    }
    
    /**
     * Creates a sample chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return The chart.
     */
    private JFreeChart createChart(final CategoryDataset dataset) {
        
        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
            "Runtime Comparison Over 100 Statements",         // chart title
            "Workload",               // domain axis label
            "Runtime (ms)",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setLowerBound(0);
        rangeAxis.setUpperBound(2000000);
        
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        // disable bar outlines...
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        
        // set up gradient paints for series...
        final GradientPaint gp0 = new GradientPaint(
            0.0f, 0.0f, Color.blue, 
            0.0f, 0.0f, Color.lightGray
        );
        final GradientPaint gp1 = new GradientPaint(
            0.0f, 0.0f, Color.green, 
            0.0f, 0.0f, Color.lightGray
        );
        final GradientPaint gp2 = new GradientPaint(
            0.0f, 0.0f, Color.red, 
            0.0f, 0.0f, Color.lightGray
        );
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );
        
        return chart;
        
    }
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

//        final BarChart demo = new BarChart("Bar Chart Demo");
        HashMap<String, Long> updateHeavy = new HashMap<String, Long>();
        updateHeavy.put("MySQL", 1781140L);
        updateHeavy.put("MongoDB", 424985L);

        HashMap<String, Long> readMostly = new HashMap<String, Long>();
        readMostly.put("MySQL", 272406L);
        readMostly.put("MongoDB", 125597L);

        HashMap<String, Long> readOnly = new HashMap<String, Long>();
        readOnly.put("MySQL", 188062L);
        readOnly.put("MongoDB", 14703L);

        HashMap<String, Long> readLatest = new HashMap<String, Long>();
        readLatest.put("MySQL", 181164L);
        readLatest.put("MongoDB", 20025L);

        HashMap<String, Long> shortRanges = new HashMap<String, Long>();
        shortRanges.put("MySQL", 166806L);
        shortRanges.put("MongoDB", 36186L);

        HashMap<String, Long> readModifyWrite = new HashMap<String, Long>();
        readModifyWrite.put("MySQL", 836876L);
        readModifyWrite.put("MongoDB", 314502L);

        HashMap<String, HashMap<String, Long>> results = new HashMap<String, HashMap<String, Long>>();
        results.put("Update Heavy", updateHeavy);
        results.put("Read Mostly", readMostly);
        results.put("Read Only", readOnly);
        results.put("Read Latest", readLatest);
        results.put("Short Ranges", shortRanges);
        results.put("Read Modify Write", readModifyWrite);

        final BarChart benchmark = new BarChart("Benchmark", results);

        benchmark.pack();
        RefineryUtilities.centerFrameOnScreen(benchmark);
        benchmark.setVisible(true);

    }

}
    
package se.chalmers.datx02_15_36.studeraeffektivt.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.StatsActivity;

/**
 * Created by Patricia on 2015-02-27.
 */
public class PieChartFragment extends Fragment {

    private static int[] COLORS = new int[] {Color.BLUE, Color.MAGENTA};

    private static double[] VALUES = new double[] {60, 40};

    private static String[] NAME_LIST = new String[] {"Klar", "Kvar"};

    private CategorySeries mSeries = new CategorySeries("");
    private DefaultRenderer mRenderer = new DefaultRenderer();
    private GraphicalView mChartView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_piechart);

        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(100, 255, 255, 255));
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsTextSize(15);
        mRenderer.setLegendTextSize(15);
        mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setStartAngle(90);

        for (int i = 0; i < VALUES.length; i++) {
            mSeries.add(NAME_LIST[i] + " " + VALUES[i], VALUES[i]);
            SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
            renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
            mRenderer.addSeriesRenderer(renderer);
        }

        if (mChartView != null) {
            mChartView.repaint();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_piechart, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) getView().findViewById(R.id.chart);
            //mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
            mRenderer.setClickEnabled(true);
            mRenderer.setSelectableBuffer(10);

            mChartView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();

                    if (seriesSelection == null) {
                        Toast.makeText(StatsActivity.this, "No chart element was clicked", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(StatsActivity.this,"Chart element data point index "+ (seriesSelection.getPointIndex()+1) + " was clicked" + " point value="+ seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
                    }*/
                }
            });

            mChartView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
                    if (seriesSelection == null) {
                        //Toast.makeText(StatsActivity.this,"No chart element was long pressed", Toast.LENGTH_SHORT);
                        return false;
                    }
                    else {
                        //Toast.makeText(StatsActivity.this,"Chart element data point index "+ seriesSelection.getPointIndex()+ " was long pressed",Toast.LENGTH_SHORT);
                        return true;
                    }
                }
            });
            layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        }
        else {
            mChartView.repaint();
        }
    }
}

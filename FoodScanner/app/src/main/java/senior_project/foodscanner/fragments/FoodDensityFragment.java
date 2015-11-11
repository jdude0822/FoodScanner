package senior_project.foodscanner.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import senior_project.foodscanner.FoodItem;
import senior_project.foodscanner.R;

/**
 * Created by Tyler on 11/3/2015.
 *
 * Dialog that appears if mass needs to be calculated.
 */
public class FoodDensityFragment extends DialogFragment {

    public FoodItem food;
    private View view;

    public static FoodDensityFragment newInstance(FoodItem food) {
        FoodDensityFragment frag = new FoodDensityFragment();
        frag.food = food;
        return frag;
    }

    public interface FoodDensityDialogListener {
        public void onDensityDialogPositiveClick(DialogFragment dialog);
        public void onDensityDialogNeutralClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver Density events
    FoodDensityDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (FoodDensityDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        view = inflater.inflate(R.layout.food_density_dialog, null);

        builder.setView(view)
                // Add Density buttons
                .setPositiveButton("Scan Food", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDensityDialogPositiveClick(FoodDensityFragment.this);
                    }
                })
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDensityDialogNeutralClick(FoodDensityFragment.this);
                    }
                });

        TextView densityTitle = (TextView) view.findViewById(R.id.densityTitle);
        densityTitle.setText(Html.fromHtml("<b>Density Info</b>"));

        final TextView densityValue = (TextView) view.findViewById(R.id.densityValue);
        final TextView densityEntry = (TextView) view.findViewById(R.id.densityEntry);
        final AutoCompleteTextView autoView = (AutoCompleteTextView)view.findViewById(R.id.densitySearch);

        // If density not yet found, display default text for density
        if (food.getDensity() == 0.0) {
            densityValue.setText(Html.fromHtml("<b>Value:</b> Please search for entry."));
            densityEntry.setText(Html.fromHtml("<b>Entry:</b> Please search for entry."));
        } else {
            densityValue.setText(Html.fromHtml("<b>Value:</b> " + food.getDensity() + " g/ml"));
            densityEntry.setText(Html.fromHtml("<b>Entry:</b> " + food.getDensityName()));
        }

        // Set up AutoCompleteTextView for densities
        String[] densityKeys = FoodItem.getDensityKeys();

        final AutoCompleteTextView auto =(AutoCompleteTextView)view.findViewById(R.id.densitySearch);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, densityKeys);
        auto.setAdapter(adapter);
        auto.setThreshold(1);
        auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // User selected density entry to use for this FoodItem

                String selectedEntry = auto.getText().toString();
                food.setDensityName(selectedEntry);
                food.setDensity(FoodItem.getDensityValue(selectedEntry));

                // Update dialog
                densityValue.setText(Html.fromHtml("<b>Value:</b> " + food.getDensity() + " g/ml"));
                densityEntry.setText(Html.fromHtml("<b>Entry:</b> " + food.getDensityName()));
            }
        });

        return builder.create();
    }


}
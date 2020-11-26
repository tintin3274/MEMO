package th.ku.memo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import th.ku.memo.R;
import th.ku.memo.activity.DayActivity;
import th.ku.memo.model.Day;

import java.util.List;

public class DayItemAdapter extends RecyclerView.Adapter<DayItemAdapter.ViewHolder>{
    private static List<Day> days;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout itemDayBackground;
        private final TextView itemDayCalculationTime;
        private final TextView itemDayName;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, DayActivity.class);
                    intent.putExtra("day", days.get(getLayoutPosition()));
                    context.startActivity(intent);

//                    Toast toast = Toast.makeText(view.getContext(), "Click "+getLayoutPosition(), Toast.LENGTH_LONG);
//                    toast.show ();
                }
            });
            itemDayBackground = view.findViewById(R.id.itemDayBackground);
            itemDayCalculationTime = view.findViewById(R.id.itemDayCalculationTime);
            itemDayName = view.findViewById(R.id.itemDayName);
        }

        public ConstraintLayout getItemDayBackground() {
            return itemDayBackground;
        }
        public TextView getItemDayCalculationTime() {
            return itemDayCalculationTime;
        }
        public TextView getItemDayName() {
            return itemDayName;
        }
    }

    public DayItemAdapter(List<Day> days) {
        this.days = days;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DayItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_day, viewGroup, false);

        return new DayItemAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DayItemAdapter.ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getItemDayBackground().setBackgroundColor(Color.parseColor(days.get(position).getColorBackground()));
        viewHolder.getItemDayCalculationTime().setTextColor(Color.parseColor(days.get(position).getColorText()));
        viewHolder.getItemDayName().setTextColor(Color.parseColor(days.get(position).getColorText()));
        viewHolder.getItemDayCalculationTime().setText(days.get(position).getCalculationTime());
        viewHolder.getItemDayName().setText(days.get(position).getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return days.size();
    }
}

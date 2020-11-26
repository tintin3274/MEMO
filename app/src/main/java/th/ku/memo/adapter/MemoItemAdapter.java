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
import th.ku.memo.activity.MemoActivity;
import th.ku.memo.model.Memo;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MemoItemAdapter extends RecyclerView.Adapter<MemoItemAdapter.ViewHolder>{
    private static List<Memo> memos;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout itemMemoBackground;
        private final TextView itemMemoText;
        private final TextView itemMemoLastUpdate;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, MemoActivity.class);
                    intent.putExtra("memo", memos.get(getLayoutPosition()));
                    context.startActivity(intent);

//                    Toast toast = Toast.makeText(view.getContext(), "Click "+getLayoutPosition(), Toast.LENGTH_LONG);
//                    toast.show ();
                }
            });
            itemMemoBackground = view.findViewById(R.id.itemDayBackground);
            itemMemoText = view.findViewById(R.id.itemDayCalculationTime);
            itemMemoLastUpdate = view.findViewById(R.id.itemDayName);
        }

        public ConstraintLayout getItemMemoBackground() {
            return itemMemoBackground;
        }

        public TextView getItemMemoText() {
            return itemMemoText;
        }

        public TextView getItemMemoLastUpdate() {
            return itemMemoLastUpdate;
        }
    }

    public MemoItemAdapter(List<Memo> memos) {
        this.memos = memos;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MemoItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_memo, viewGroup, false);

        return new MemoItemAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MemoItemAdapter.ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getItemMemoBackground().setBackgroundColor(Color.parseColor(memos.get(position).getColorBackground()));
        viewHolder.getItemMemoText().setTextColor(Color.parseColor(memos.get(position).getColorText()));
        viewHolder.getItemMemoLastUpdate().setTextColor(Color.parseColor(memos.get(position).getColorText()));
        viewHolder.getItemMemoText().setText(memos.get(position).getText());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        viewHolder.getItemMemoLastUpdate().setText("Last Update: "+memos.get(position).getTimeUpdate().format(formatter));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return memos.size();
    }
}

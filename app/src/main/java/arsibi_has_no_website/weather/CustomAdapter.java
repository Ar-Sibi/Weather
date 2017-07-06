package arsibi_has_no_website.weather;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hp on 02-07-2017.
 */

public class CustomAdapter extends ArrayAdapter<ListItem> {
    Context c;
    public CustomAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ListItem> objects) {
        super(context, resource, objects);
        c=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View v= inflater.inflate(R.layout.item,parent,false);
        TextView tv=(TextView)v.findViewById(R.id.iditem);
        tv.setText(getItem(position).id);
        tv=(TextView)v.findViewById(R.id.valueitem);
        tv.setText(getItem(position).value);
        return v;
    }
}

package lmq.tongdao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.List;

/**
 * Created by quanj on 2017.12.20.
 */

public class MarkMovieAdapter extends ArrayAdapter<MarkMovieUnit> {

    private int resourceId;
    /**
     *context:当前活动上下文
     *textViewResourceId:ListView子项布局的ID
     *objects：要适配的数据
     */
    public MarkMovieAdapter(Context context, int textViewResourceId,
                            List<MarkMovieUnit> objects) {
        super(context, textViewResourceId, objects);
        //拿取到子项布局ID
        resourceId = textViewResourceId;
    }

    /**
     * LIstView中每一个子项被滚动到屏幕的时候调用
     * position：滚到屏幕中的子项位置，可以通过这个位置拿到子项实例
     * convertView：之前加载好的布局进行缓存
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MarkMovieUnit mmu = getItem(position);  //获取当前项的Fruit实例
        //为子项动态加载布局
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        CheckBox checkbox = (CheckBox)  view.findViewById(R.id.mark_listview_item_checkbox);
        checkbox.setText(mmu.getMovieName());
        checkbox.setChecked(mmu.getMarked());

        return view;
    }


}

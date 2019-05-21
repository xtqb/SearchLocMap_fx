package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.TreeStateBean;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.view.ShowStateDetailDialog;

import java.util.List;

/**
 * Created by xtqb on 2019/4/19.
 */
public class TreeStateListActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView im_back;
    private SwipeMenuListView listview;
    private final int DELETE = 0;
    private final int STATE = 1;
    private List<TreeStateBean> list;
    private DatabaseHelper helper;
    private Dao<TreeStateBean, Integer> treeDao;
    private int total;
    private int success;
    private int fail;
    private String body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_state);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        im_back.setOnClickListener(this);
        listview.setAdapter(adapter);
        listview.setOnMenuItemClickListener(listener);
        listview.setOnItemClickListener(this);
        listview.setMenuCreator(creator);
    }

    private void initData() {
        helper = DatabaseHelper.getHelper(TreeStateListActivity.this);
        treeDao = helper.getTreeStateDao();
        list = CommonDBOperator.queryAllItemByOrder(treeDao, "time");
    }

    private void initView() {
        im_back = (ImageView) findViewById(R.id.im_back);
        listview = (SwipeMenuListView) findViewById(R.id.listview);
    }

    // 第1步：设置创建器，并且在其中生成我们需要的菜单项，将其添加进菜单中
    private SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // 创建“打开”项
            SwipeMenuItem cancelItem = new SwipeMenuItem(TreeStateListActivity.this);
            cancelItem.setBackground(new ColorDrawable(Color
                    .rgb(0xF9, 0x3F, 0x25)));
            cancelItem.setWidth(dp2px(90));
            cancelItem.setTitle(getString(R.string.delete));
            cancelItem.setTitleSize(16);
            cancelItem.setTitleColor(Color.WHITE);
            // 将创建的菜单项添加进菜单中
            menu.addMenuItem(cancelItem);

//            // 创建“删除”项
//            SwipeMenuItem deleteItem = new SwipeMenuItem(TreeStateListActivity.this);
//            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
//            deleteItem.setWidth(dp2px(90));
//            deleteItem.setTitle(getString(R.string.upload_state));
//            deleteItem.setTitleSize(16);
//            deleteItem.setTitleColor(Color.WHITE);
//            // 将创建的菜单项添加进菜单中
//            menu.addMenuItem(deleteItem);
        }
    };

    private SwipeMenuListView.OnMenuItemClickListener listener = new SwipeMenuListView.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(int position, SwipeMenu menu, int menuId) {

            // TODO Auto-generated method stub
            Log.e("Tag", menuId + "");
            switch (menuId) {
                case DELETE:
                    deleteItem(position);
                    break;
            }
            return false;
        }
    };

    private void deleteItem(int pos) {
        CommonDBOperator.deleteByKeys(treeDao, "SEND_ID", list.get(pos).getSEND_ID() + "");
        if (list != null && list.size() > 0) {
            list.clear();
        }
        list = CommonDBOperator.getList(treeDao);
        adapter.notifyDataSetChanged();
    }

    private void showDetailDialog(int pos) {
        ShowStateDetailDialog dialog = new ShowStateDetailDialog(TreeStateListActivity.this, total, success, fail, body);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getWindowManager().getDefaultDisplay().getWidth() - 80;
        params.height = 782;
        dialog.getWindow().setAttributes(params);
        TreeStateBean item = list.get(pos);
        String[] data = new String[]{item.getsMonth(), item.getsTime(), item.getsMonth(), item.getsTime(), item.getcMonth(), item.getcTime(), item.getsState(), item.getNum(), item.getsState(), item.getcState()};
        dialog.refleshView(data);
    }

    private BaseAdapter adapter = new BaseAdapter() {

        private ViewHolder holder;

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (list == null) return 0;
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = LayoutInflater.from(TreeStateListActivity.this).inflate(R.layout.item_history_state, null);
                holder = new ViewHolder();
                holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_num.setText((position + 1) + "");
            holder.tv_time.setText(BaseUtils.getDateStr(list.get(position).getTime()));
            holder.tv_state.setText("2".equals(list.get(position).getcState()) ? getString(R.string.upload_success_2) : getString(R.string.upload_being));
            return convertView;
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        total = list.get(position).getTotal();
        success = list.get(position).getSuccessNum();
        fail = list.get(position).getFailNum();
        body = list.get(position).getContent();
        showDetailDialog(position);
    }

    private class ViewHolder {
        private TextView tv_num;
        private TextView tv_time;
        private TextView tv_state;
    }


    // 另一种将dp转换为px的方法
    protected int dp2px(float value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_back:
                this.finish();
                break;
            case R.id.listview:
                break;
        }
    }
}

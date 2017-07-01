package com.test.qqeffectsdemo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.qqeffectsdemo.R;
import com.test.qqeffectsdemo.utils.Constant;
import com.test.qqeffectsdemo.view.MyLiearLayout;
import com.test.qqeffectsdemo.view.ParallaxLayout;
import com.test.qqeffectsdemo.view.SlideMenuView;
import com.test.qqeffectsdemo.view.Swipedelete;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.menu_listview)
    ListView menuListview;
    @BindView(R.id.main_listview)
    ParallaxLayout mainListview;
    @BindView(R.id.slide_main)
    SlideMenuView activityMain;
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.mylayout)
    MyLiearLayout mylayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        View headerView = View.inflate(MainActivity.this, R.layout.header_layout, null);
        menuListview.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.WHITE);
                return view;
            }
        });
        mylayout.setSlideMenu(activityMain);
        ImageView image = (ImageView) headerView.findViewById(R.id.img);
        mainListview.addHeaderView(headerView);
        mainListview.setImageView(image);
        mainListview.setAdapter(new MyAdapter());


        mainListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (swipedelete != null) {
                    swipedelete.close();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        activityMain.setOnSlideChangeListener(new SlideMenuView.OnSlideChangeListener() {
            @Override
            public void onDragging(float parcent) {
//                Log.e("tag", "onDragging: " + parcent);
                if (parcent!=0&&swipedelete!=null){
                    swipedelete.close();
                }
            }

            @Override
            public void onOpen() {
                Toast.makeText(MainActivity.this, "打开", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClose() {
                Toast.makeText(MainActivity.this, "关闭", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private Swipedelete swipedelete;

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Constant.NAMES.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item_adapter, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String name = Constant.NAMES[position];
            holder.tvName.setText(name);
            holder.swi.setOnSwipeListener(new Swipedelete.OnSwipeListener() {
                @Override
                public void onOpen(Swipedelete layout) {
                    //当swipedelete中不是自己是时,关闭横拉删除view
                    if (swipedelete != null && swipedelete != layout) {
                        swipedelete.close();
                    }
                    swipedelete = layout;
                    Log.e("diaoyongle",  "调用了"+"onOpen");
                }

                @Override
                public void onClose(Swipedelete layout) {
                    if (layout == swipedelete) {
                        swipedelete = null;
                        Log.e("diaoyongle", "调用了"+"onClose");
                    }
                }
            });


            holder.swi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, Constant.NAMES[position], Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.tv_name)
            TextView tvName;
            @BindView(R.id.tv_delete)
            TextView tvDelete;
            @BindView(R.id.swi)
            Swipedelete swi;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}

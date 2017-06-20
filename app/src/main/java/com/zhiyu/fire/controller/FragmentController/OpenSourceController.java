package com.zhiyu.fire.controller.FragmentController;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhiyu.fire.R;
import com.zhiyu.fire.ui.OpenSourceLicenseFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/6/19 0019.
 * 开源许可证页面的控制器
 */

public class OpenSourceController implements OpenSourceLicenseFragment.IOpenSource {

    @Override
    public void initData(AppCompatActivity activity, RecyclerView recyclerView) {
        new Adapter(activity, new Observer<Adapter>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Adapter adapter) {
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private static class Adapter extends RecyclerView.Adapter<OSLViewHolder> {

        //展示开源许可证的RecyclerView的适配器

        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private List<OSL> mData;
        private Observer<Adapter> mObserver;

        Adapter(Context context, Observer<Adapter> observer) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            mObserver = observer;
            mData = new ArrayList<>();
            init();
        }

        private void init() {
            Observable<Adapter> observable = Observable.create(e -> {
                String[] titles = {"OkHttp", "Retrofit", "Gson", "Glide",
                        "Subsampling Scale Image View", "EventBus", "RxJava", "RxAndroid", "致谢"};
                int i = 0;
                InputStream inputStream = null;
                BufferedReader bufferedReader = null;
                StringBuilder content = new StringBuilder();
                try {
                    inputStream = mContext.getAssets().open("open_source.txt");
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.equals("*")) {
                            OSL osl = new OSL();
                            osl.setTitle(titles[i]);
                            i++;
                            osl.setContent(content.toString());
                            content.delete(0, content.length());
                            mData.add(osl);
                        } else {
                            content.append("\n");
                            content.append(line);
                        }
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                        e.onNext(this);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            });
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mObserver);
        }

        @Override
        public OSLViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new OSLViewHolder(mLayoutInflater.inflate(R.layout.item_open_source, parent, false));
        }

        @Override
        public void onBindViewHolder(OSLViewHolder holder, int position) {
            holder.tvOPLTitle.setText(mData.get(position).getTitle());
            holder.tvOPLContent.setText(mData.get(position).getContent());
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private static class OSLViewHolder extends RecyclerView.ViewHolder {

        TextView tvOPLTitle;
        TextView tvOPLContent;

        OSLViewHolder(View view) {
            super(view);
            tvOPLTitle = (TextView) view.findViewById(R.id.open_source_title);
            tvOPLContent = (TextView) view.findViewById(R.id.open_source_content);
        }

    }

    private static class OSL {

        //开源许可证的实体类，包括标题和许可证内容

        String title;
        String content;

        String getTitle() {
            return title;
        }

        void setTitle(String title) {
            this.title = title;
        }

        String getContent() {
            return content;
        }

        void setContent(String content) {
            this.content = content;
        }

    }

}
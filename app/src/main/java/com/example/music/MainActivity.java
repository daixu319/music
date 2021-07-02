package com.example.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView nextIv,startIv,lastIv;
    TextView songTv,singerTv;
    RecyclerView rv;
//    数据源
    List<LocalMusicBean>mDatas;
    private LocalMusicAdapter adapter;

//    记录当前正在播放的音乐的位置
    int currnetPlayPosition = -1;
//    记录暂停音乐时进度条的位置
    int currentPausePositionInSong = 0;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mediaPlayer = new MediaPlayer();
        mDatas = new ArrayList<>(); //初始化
        //创建适配器
        adapter = new LocalMusicAdapter(this, mDatas); //成员变量
        rv.setAdapter(adapter);
        // 设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        // 加载本地数据源
        loadLocalMusicData();
        //设置每一项的点击事件
        setEventListener();
    }

    private void setEventListener() {
        /* 设置每一项的点击事件 */
        adapter.setOnItemClickListener(new LocalMusicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                currnetPlayPosition = position;
                LocalMusicBean musicBean = mDatas.get(position);
                playMusicInMusicBean(musicBean);
            }
        });
    }

    public void playMusicInMusicBean(LocalMusicBean musicBean) {
//        根据传入对象播放音乐
        singerTv.setText(musicBean.getSinger()); //设置底部显示的歌手和歌曲名
        songTv.setText(musicBean.getSong());
        stopMusic();
//                重置多媒体播放器
        mediaPlayer.reset();
//                设置新的播放路径
        try {
            mediaPlayer.setDataSource(musicBean.getPath());
            playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    点击播放按钮播放音乐,或者暂停重新播放
    private void playMusic() {
        /* 播放音乐的函数 */
        if (mediaPlayer!=null&&!mediaPlayer.isPlaying()){
            if (currentPausePositionInSong == 0) {
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
//                从暂停到播放
                mediaPlayer.seekTo(currentPausePositionInSong);
                mediaPlayer.start();
            }
            startIv.setImageResource(R.drawable.pause);
        }
    }

    private void PauseMusic() {
        /* 暂停音乐的函数*/
        if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
            currentPausePositionInSong = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            startIv.setImageResource(R.drawable.start);
        }
    }

    private void stopMusic() {
//                停止音乐的函数
        if (mediaPlayer!=null){
            currentPausePositionInSong = 0;
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer.stop();
            startIv.setImageResource(R.drawable.start);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
    }


    private void loadLocalMusicData() {
        /*加载本地存储的音乐文件到集合当中*/
        ContentResolver resolver = getContentResolver(); // 获取ContentResolver对象
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; //获取本地音乐存储的地址
        Cursor cursor = resolver.query(uri, null, null, null, null);//查询地址
        int id = 0;
        while (cursor.moveToNext()) {                       //遍历cursor
            String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            id++;
            String sid = String.valueOf(id); //歌曲的序号
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String t = sdf.format(new Date(time));
            LocalMusicBean bean = new LocalMusicBean(sid, song, singer, t, path); //将一行当中的数据封装到对象当中
            mDatas.add(bean);
        }
        adapter.notifyDataSetChanged(); //数据源变化，提示适配器更新
    }


    private void initView() {
        /*初始化控件的函数*/
        nextIv = findViewById(R.id.next);
        startIv = findViewById(R.id.start);
        lastIv = findViewById(R.id.last);
        singerTv = findViewById(R.id.singer);
        songTv = findViewById(R.id.song);
        rv = findViewById(R.id.local_rv);
        nextIv.setOnClickListener(this);
        startIv.setOnClickListener(this);
        lastIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.last:
                if (currnetPlayPosition == 0) {
                    Toast.makeText(this,"已经是第一首了没有上一首！",Toast.LENGTH_SHORT);
                    return;
                }
                currnetPlayPosition = currnetPlayPosition-1;
                LocalMusicBean lastBean = mDatas.get(currnetPlayPosition);
                playMusicInMusicBean(lastBean);
                break;
            case R.id.next:
                if (currnetPlayPosition == mDatas.size()-1) {
                    Toast.makeText(this,"已经是最后一首了，没有下一首！",Toast.LENGTH_SHORT);
                    return;
                }
                currnetPlayPosition = currnetPlayPosition+1;
                LocalMusicBean nextBean = mDatas.get(currnetPlayPosition);
                playMusicInMusicBean(nextBean);
                break;
            case R.id.start:
                if (currnetPlayPosition == -1) {
//                    并没有选中要播放的音乐
                    Toast.makeText(this,"请选择想要播放的音乐",Toast.LENGTH_SHORT);
                    return;
                }
                if (mediaPlayer.isPlaying()) {
//                    此时处于播放状态，需要暂停音乐
                    PauseMusic();
                }else {
//                    此时没有播放音乐，点击开始播放音乐
                    playMusic();
                }
                break;
        }
    }


}
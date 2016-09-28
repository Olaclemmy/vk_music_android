package com.icapps.vkmusic.base;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.icapps.vkmusic.VkApplication;
import com.icapps.vkmusic.service.MusicService;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VkAudioArray;

import javax.inject.Inject;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by maartenvangiel on 26/09/16.
 */
public abstract class BaseMusicFragment extends BaseFragment implements ServiceConnection, MusicService.MusicServiceListener {
    @Inject public ObservableField<VKApiAudio> currentAudio;
    @Inject public ObservableField<String> currentAlbumArtUrl;
    @Inject public VkAudioArray playbackQueue;

    private Observable.OnPropertyChangedCallback currentAudioCallback;
    private Observable.OnPropertyChangedCallback currentAlbumArtCallback;

    protected MusicService musicService;
    protected boolean musicServiceBound;

    protected abstract void injectDependencies();

    @Override
    protected void inject() {
        ((VkApplication) getActivity().getApplication()).getUserComponent().inject(this);
        injectDependencies();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentAudioCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                onCurrentAudioChanged(currentAudio.get());
            }
        };
        currentAlbumArtCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                onCurrentAlbumArtChanged(currentAlbumArtUrl.get());
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getContext(), MusicService.class);
        getContext().bindService(intent, this, BIND_AUTO_CREATE);
        currentAudio.addOnPropertyChangedCallback(currentAudioCallback);
        currentAlbumArtUrl.addOnPropertyChangedCallback(currentAlbumArtCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        musicService.removeMusicServiceListener(this);
        getContext().unbindService(this);
        currentAudio.removeOnPropertyChangedCallback(currentAudioCallback);
        currentAlbumArtUrl.removeOnPropertyChangedCallback(currentAlbumArtCallback);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        musicService = ((MusicService.MusicServiceBinder) service).getService();
        musicService.addMusicServiceListener(this);
        musicServiceBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicServiceBound = false;
        musicService = null;
    }

    @Override
    public void onFinishRequested() {
        getActivity().finish();
    }

    protected void onCurrentAudioChanged(VKApiAudio currentAudio) {
        // Implement in subclass (optional)
    }

    protected void onCurrentAlbumArtChanged(String currentAlbumArtUrl) {
        // Implement in subclass (optional)
    }

    @Override
    public void onMusicServiceException(Exception ex) {
        // Implement in subclass (optional)
    }

    @Override
    public void onPlaybackStateChanged(MusicService.PlaybackState state) {
        // Implement in subclass (optional)
    }

    @Override
    public void onPlaybackPositionChanged(int position) {
        // Implement in subclass (optional)
    }

    @Override
    public void onPlaybackQueueChanged() {
        // Implement in subclass (optional)
    }
}

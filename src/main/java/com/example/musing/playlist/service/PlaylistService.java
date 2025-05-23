package com.example.musing.playlist.service;

import com.example.musing.common.dto.ResponseDto;
import com.example.musing.playlist.dto.*;
import com.example.musing.playlist.entity.PlayList;
import com.example.musing.user.entity.User;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


public interface PlaylistService {
    void removePlaylist(String playlistId) throws IOException, GeneralSecurityException, InterruptedException;
    void removeVideoFromYoutubePlaylist(String playlistId, List<String> deleteVideoLinks)
            throws IOException, GeneralSecurityException, InterruptedException;
    void modifyPlaylist(YoutubePlaylistRequestDto dto, String playlistId, List<String> deleteVideoLinks);
    void modifyYoutubePlaylistInfo(YoutubePlaylistRequestDto dto, String playlistId)
            throws IOException, InterruptedException, GeneralSecurityException;
    YouTubeVideoResponse getVideoInfo(String videoId);

    String checkUrl(String url);

     PlaylistResponse getUserPlaylist(String url);
    String getPlayTime(String url);
    String getThumailLink(String url);

    String addVideoToPlaylist(String accessToken, YoutubeVideoRequestDto dto);
    SelectPlayListsDto selectMyPlayList();
    String addMusicToPlaylist(String url,String playlistId);
    void addNewPlaylist(String listName,String description);
    PlaylistResponse SelectMyDBPlaylist(String listId);

}

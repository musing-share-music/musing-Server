package com.example.musing.playlist.service;

import com.example.musing.playlist.dto.*;
import com.example.musing.playlist.entity.PlayList;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


public interface PlaylistService {
    void syncPlaylistWithDB(String url);
    void removePlaylist(String playlistId);
    void removeVideoFromYoutubePlaylist(String playlistId, List<String> deleteVideoLinks)
            throws IOException, GeneralSecurityException, InterruptedException;
    void modifyPlaylist(YoutubePlaylistRequestDto dto, String playlistId, List<String> deleteVideoLinks);
    void modifyYoutubePlaylistInfo(YoutubePlaylistRequestDto dto, PlayList playlist)
            throws IOException, InterruptedException, GeneralSecurityException;
    YouTubeVideoResponse getVideoInfo(String videoId);

    String checkUrl(String url);

    PlaylistResponse getUserPlaylist(String url);
    String getPlayTime(String url);
    String getThumbnailLink(String url);

    String addVideoToPlaylist(String accessToken, YoutubeVideoRequestDto dto);
    SelectPlayListsDto selectMyPlayList();
    String addMusicToPlaylist(String url,String playlistId);
    void addNewPlaylist(String listName,String description) throws IOException, GeneralSecurityException, InterruptedException ;
    PlaylistResponse SelectMyDBPlaylist(String listId);
    List<PlaylistResponse> selectMyAllPlayListInfo();

}

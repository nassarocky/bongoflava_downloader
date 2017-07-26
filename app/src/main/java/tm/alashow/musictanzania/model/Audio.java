/*
 * Copyright 2015. Alashov Berkeli
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package tm.alashow.musictanzania.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Locale;

import tm.alashow.musictanzania.Config;
import tm.alashow.musictanzania.util.U;

/**
 * Created by alashov on 07/05/15.
 */
@IgnoreExtraProperties
public class Audio {

    public long id;
    public String duration;



    public long ownerId;

    public String date;


    public String artist;


    public String name;


    public String title;


    public String downloadUrl
            ;
    public long bytes = - 1;

    public Audio() {
    }
    public Audio(String upload,String name,String title) {
        this.downloadUrl=upload;
        this.name=name;
        this.title=title;
    }

    public String getEncodedAudioId() {
        return String.format(Locale.ROOT, "%s:%s", U.encode(ownerId), U.encode(id));
    }
public  void setDownloadUrl(String downloadUrl){
    this.downloadUrl=downloadUrl;
}
    public String getStreamUrl() {
        return downloadUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getDownloadUrl(int bitrate) {
        if (! Config.isBitrateAllowed(bitrate)) {
            return getDownloadUrl();
        }
        return String.format(Locale.ROOT, "%s/%d", getDownloadUrl(), bitrate);
    }

    public String getFileSize(){
        return U.humanReadableByteCount(getBytes(), false);
    }

    public String getSafeFileName(int bitrate) {
        String audioName = getFullName();
        if (audioName.length() > 100) {
            audioName = audioName.substring(0, 100);
        }

        if (bitrate > 0) {
            audioName += String.format(Locale.ROOT, " (%d)", bitrate);
        }
        audioName += ".mp3";

        return name;
    }

    public String getFullName() {
        return artist+" "+"-"+" "+name;
    }

    public long getId() {
        return id;
    }

    public Audio setId(long id) {
        this.id = id;
        return this;
    }

    public String getDuration() {
        return duration;
    }

    public Audio setDuration(int duration) {
      // this.duration = duration;
        return this;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public Audio setOwnerId(long ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public String getArtist() {
        return artist;
    }

    public Audio setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public String getTitle() {

        return name;
    }

    public Audio setTitle(String title) {
        this.title = title;
        return this;
    }

    public long getBytes() {
        return bytes;
    }

    public Audio setBytes(long bytes) {
        this.bytes = bytes;
        return this;
    }
}

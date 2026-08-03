package com.kwork.nusic.data;

public class SettingsData {

    /*
     * Перемешивание треков
     */
    public boolean shuffle = false;

    /*
     * Повтор всего плейлиста
     */
    public boolean repeatPlaylist = false;

    /*
     * Повтор одного трека
     */
    public boolean repeatTrack = false;

    /*
     * Переходить автоматически к следующему треку
     */
    public boolean autoNext = true;

    /*
     * Громкость музыки
     */
    public float volume = 1.0f;

    /*
     * Показывать нижнюю панель плеера
     */
    public boolean showPlayerBar = true;

    /*
     * Запускать музыку после входа в мир
     */
    public boolean playOnJoin = false;

    /*
     * Автоматически обновлять библиотеку
     */
    public boolean autoScan = true;

    /*
     * Плавное переключение треков
     */
    public boolean fadeBetweenTracks = true;

    /*
     * Последний открытый трек
     */
    public String lastTrack = "";

    /*
     * Последняя позиция воспроизведения
     */
    public long lastPosition = 0;

    public SettingsData(){

    }

}
package me.schooltests.potatoolympics.core.data;

public interface IGame {
    String getName();
    void start(String map);
    void end();
}
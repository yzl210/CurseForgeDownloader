package cn.leomc.cfdownloader.progress;

public interface ProgressTracker {

    ProgressBarControl main();

    ProgressBarControl create();

    void remove(ProgressBarControl bar);

}

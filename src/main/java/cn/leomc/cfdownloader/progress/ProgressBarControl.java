package cn.leomc.cfdownloader.progress;

public interface ProgressBarControl {

    void setStatus(String status);

    void setProgress(double progress);

    default void set(String status, double progress){
        setStatus(status);
        setProgress(progress);
    }

    void remove();

}

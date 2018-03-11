package lmq.tongdao;

/**
 * Created by quanj on 2017.12.20.
 */

public class MarkMovieUnit {
    private String movie_name = "";
    private boolean marked = false;
    private boolean origin = false;

    public MarkMovieUnit(String movie_name, boolean marked) {
        this.movie_name = movie_name;
        this.marked = marked;
        this.origin = marked;
    }

    public String getMovieName() {
        return movie_name;
    }

    public boolean getMarked() {
        return marked;
    }

    public void setMarked(boolean v) {
        marked = v;
    }

    public boolean isChanged() {
        return this.marked != this.origin;
    }

}

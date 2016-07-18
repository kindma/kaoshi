package lh.sichuangsoft.cn.kaoshi;

/**
 * Created by 85832_000 on 2016/7/15.
 */
public class Question {
    private String _id;
    private String q;
    private String a;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }



    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }



    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public Question(String _id, String q, String a) {
        super();
        this._id = _id;
        this.q = q;
        this.a = a;
    }

    @Override
    public String toString() {
        return  _id + "," + q  ;
    }

}

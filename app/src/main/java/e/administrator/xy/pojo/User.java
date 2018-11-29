package e.administrator.xy.pojo;

import android.accounts.Account;

import java.io.File;

import e.administrator.xy.util.Cn2Spell;

/**
 * Created by Administrator on 2016/5/25.
 */
public class User implements Comparable<User> {

    private String account;//账号，用来查询极光后台的用户信息
    private File ava;//头像
    private String name; // 姓名
    private String pinyin; // 姓名对应的拼音
    private String firstLetter; // 拼音的首字母
    public User() {
    }

    public User(String name,String account,File ava) {
        this.ava = ava;
        this.account = account;
        this.name = name;
        pinyin = Cn2Spell.getPinYin(name); // 根据姓名获取拼音
        firstLetter = pinyin.substring(0, 1).toUpperCase(); // 获取拼音首字母并转成大写
        if (!firstLetter.matches("[A-Z]")) { // 如果不在A-Z中则默认为“#”
            firstLetter = "#";
        }
    }

    public File getAva() {
        return ava;
    }

    public String getAccount() {
        return account;
    }

    public String getName() {
        return name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getFirstLetter() {
        return firstLetter;
    }


    @Override
    public int compareTo(User another) {
        if (firstLetter.equals("#") && !another.getFirstLetter().equals("#")) {
            return 1;
        } else if (!firstLetter.equals("#") && another.getFirstLetter().equals("#")){
            return -1;
        } else {
            return pinyin.compareToIgnoreCase(another.getPinyin());
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", firstLetter='" + firstLetter + '\'' +
                '}';
    }
}

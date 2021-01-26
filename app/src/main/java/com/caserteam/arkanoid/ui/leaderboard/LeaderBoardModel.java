package com.caserteam.arkanoid.ui.leaderboard;

public class LeaderBoardModel {
    private String nickname;
    private int score;

    public LeaderBoardModel(String nickname, int score){
        this.nickname = nickname;
        this.score = score;
    }

    public LeaderBoardModel() {

    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    @Override
    public String toString() {
        return "LeaderBoardModel{" +
                "nickname='" + nickname + '\'' +
                ", score='" + score + '\'' +
                '}';
    }
}

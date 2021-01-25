package com.caserteam.arkanoid.ui.leaderboard;

public class LeaderBoardModel {
    private String nickname;
    private String score;

    public LeaderBoardModel(String nickname, String score){
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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
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

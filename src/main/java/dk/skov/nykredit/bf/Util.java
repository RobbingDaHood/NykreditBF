package dk.skov.nykredit.bf;

import dk.skov.nykredit.bf.Model.Game;
import dk.skov.nykredit.bf.Model.Player;
import dk.skov.nykredit.bf.Model.Score;
import dk.skov.nykredit.bf.Model.TotalScore;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by aogj on 10-07-15.
 * <p>
 * prettymuch only used by newGameDiv.jsp
 * <p>
 * We need to make sure the playersReadyListList is updated before presenting any data.
 */
public class Util {

    public static Game tableOne;
    public static Game tableTwo;

    private static ScoreBoardGenerator scoreBoardGenerator = ScoreBoardGenerator.getSingleton();

    public static void updateModel(HttpServletRequest request) {
        if (request.getParameter("newGame") != null) {
            LinkedList<Player> players = new LinkedList<>(scoreBoardGenerator.getAllPlayers().values());
            Collections.sort(players);

            List<Player> choosenPlayers = new LinkedList<>();
            for (Player player : players) {
                if (player.isReady()) {
                    choosenPlayers.add(player);
                }

                if (choosenPlayers.size() >= 8) {
                    break;
                }
            }

            int size = choosenPlayers.size();

            Random rand = new Random();
            Player[] setup = new Player[8];
            int count = 0;
            int countMAx = size < 8 ? size : 8;
            while (count < countMAx) {
                int index = rand.nextInt(countMAx);
                if (setup[index] == null) {
                    setup[index] = choosenPlayers.get(count);
                    count++;
                }
            }

            for (int i = size; i < 8; i++) {
                setup[i] = null;
            }

            tableOne = new Game(setup[0], setup[2], setup[1], setup[3]);
            tableTwo = new Game(setup[4], setup[6], setup[5], setup[7]);
        }


        //----------

        if (request.getParameter("t1BlueWinner") != null) {
            tableOne.setRedWinner(false);
            tableOne.setTimestamp(GregorianCalendar.getInstance().getTime());
            updateScores(tableOne);
            tableOne = new Game(tableOne);
        }

        if (request.getParameter("t1RedWinner") != null) {
            tableOne.setRedWinner(true);
            tableOne.setTimestamp(GregorianCalendar.getInstance().getTime());
            updateScores(tableOne);
            tableOne = new Game(tableOne);
        }

        if (request.getParameter("t2BlueWinner") != null) {
            tableTwo.setRedWinner(false);
            tableTwo.setTimestamp(GregorianCalendar.getInstance().getTime());
            updateScores(tableTwo);
            tableTwo = new Game(tableTwo);
        }

        if (request.getParameter("t2RedWinner") != null) {
            tableTwo.setRedWinner(true);
            tableTwo.setTimestamp(GregorianCalendar.getInstance().getTime());
            updateScores(tableTwo);
            tableTwo = new Game(tableTwo);
        }
    }

    private static void updateScores(Game game) {
        SimpleDBHandler.addGame(game);
        scoreBoardGenerator.updateGame(game, true);
    }

    public static String generateScoreboard() {
        List<TotalScore> allScores = scoreBoardGenerator.getAllScores();
        StringBuffer result = new StringBuffer();

        createScoreTable(result, allScores.get(0), "Rolling Day");
        createScoreTable(result, allScores.get(1), "Rolling Week");
        createScoreTable(result, allScores.get(2), "Rolling Month");
        createScoreTable(result, allScores.get(3), "Total");

        return result.toString();
    }

    private static void createScoreTable(StringBuffer result, TotalScore allScore, String title) {
        result.append("<td valign=\"top\">");
        result.append(title);
        result.append("<table style=\"border:2px solid black;border-collapse:collapse\">\n");


        //Header
        result.append("                <tr>\n").
                append("                    <th style=\"border:1px solid black;\">#</th>\n").
                append("                    <th style=\"border:1px solid black;\">Name</th>\n").
                append("                    <th style=\"border:1px solid black;\">points</th>\n").
                append("                    <th style=\"border:1px solid black;\">games played</th>\n").
                append("                </tr>\n");

        int i = 0;
        List<Score> values = new LinkedList<>(allScore.getAllScores().values());
        Collections.sort(values);
        for (Score score : values) {
            result.append("<tr>\n").
                    append("                    <td style=\"border:1px solid black;\">").
                    append(++i).append("\n").append("                    </td>\n").
                    append("                    <td style=\"border:1px solid black;\">").
                    append(score.getPlayer().getName()).append("\n").
                    append("                    </td>\n").
                    append("                    <td style=\"border:1px solid black;\">").
                    append(score.getScore()).append("\n").
                    append("                    </td>\n").
                    append("                    <td style=\"border:1px solid black;\">").
                    append(score.getGamesPlayed()).append("\n").
                    append("                    </td>\n").
                    append("</tr>\n");
        }

        result.append("</table>");
        result.append("</td>");
    }

    public static int getBlueSumTableOne() {
        return scoreBoardGenerator.getBlueSum(tableOne);
    }

    public static int getRedSumTableOne() {
        return scoreBoardGenerator.getRedSum(tableOne);
    }

    public static int getBlueSumTableTwo() {
        return scoreBoardGenerator.getBlueSum(tableTwo);
    }

    public static int getRedSumTableTwo() {
        return scoreBoardGenerator.getRedSum(tableTwo);
    }

}

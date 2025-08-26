package common;

public class JudgeUtil {
    public static String judge(String answer, String guess) {
        int strike = 0, ball = 0;
        for (int i = 0; i < 3; i++) {
            char g = guess.charAt(i);
            if (g == answer.charAt(i)) strike++;
            else if (answer.indexOf(g) >= 0) ball++;
        }
        // 0S 0B ➜ OUT
        return (strike == 0 && ball == 0) ? "OUT" : strike + "S " + ball + "B";
    }

    public static boolean isValid(String num) {
        if (num.length() != 3) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
                char c = num.charAt(i);
                if (!Character.isDigit(c))
                {
                    return false;
                }
            if (num.indexOf(c) != num.lastIndexOf(c)){
                return false;
            }
        }
        return true;
    }
}

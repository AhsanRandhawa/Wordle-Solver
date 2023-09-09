// Abel, Ashan, Victor
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class WordleSolver {
    static double ratioForChange = 1.25;
    static boolean Optimizer;
    static double alphaValueOfGuess = 79277.23353563496;

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length < 2 || args.length > 4) {
            System.err.println("We need at least 2 command line arguments.");
            System.err.println(
                    "Try the following command: java WordleSolver filePath targetWord [Optimizer] [AutoFirstGuess]");
            System.err.println("e.g : java WordleSolver worldewords.txt apple t t");

            System.exit(1);
        }
        String filePath = args[0];
        String target = args[1];
        String guess = "";
        boolean first = false;
        if (args.length > 2) {
            Optimizer = args[2].equalsIgnoreCase("t");
            if (args.length == 4) {
                first = args[3].equalsIgnoreCase("t");
                if (first)
                    guess = "tares";
            }

        }
        HashSet<String> originalWords = readWords(filePath);

        if (!originalWords.contains(target)) {
            System.err.println("The given target is not in the dictionary.");
            System.exit(1);
        }

        HashSet<String> possibleWords = new HashSet<>();
        possibleWords = originalWords;
        if (Optimizer)
            possibleWords = (HashSet<String>) originalWords.clone();

        if (!first)
            System.out.println("Be Patient! It will take about 60 seconds to generate the first guess.");

        while (!guess.equals(target)) {
            if (!first)
                guess = generateGuess(originalWords, possibleWords);
            first = false;
            String pattern = findPattern(guess, target, generateTargetMap(target));
            printPrettyGuess(guess, pattern);
            System.out.println(", " + alphaValueOfGuess);
            possibleWords = removeNAWords(possibleWords, guess, pattern);
        }

    }

    // Check if this is correct
    private static double findAlpha(HashMap<String, Integer> patternToNumPosTargets) {
        if (patternToNumPosTargets.size() == 0)
            return Double.MAX_VALUE;
        double alpha = 0;
        for (Map.Entry<String, Integer> entry : patternToNumPosTargets.entrySet()) {
            alpha += entry.getValue() * Math.log(entry.getValue());
        }
        return alpha;
    }

    private static void printPrettyGuess(String guess, String pattern) {
        // We can switch from changing the color of backgrounds of characters to
        // actually changing color of characters
        // by replacing the 4 to a 3 in the following strings.
        final String black = "\u001B[40m";
        final String yellow = "\u001B[43m";
        final String green = "\u001B[42m";

        final String reset = "\u001B[0m";

        for (int i = 0; i < guess.length(); i++) {
            if (pattern.charAt(i) == 'B')
                System.out.print(black);
            else if (pattern.charAt(i) == 'G')
                System.out.print(green);
            else
                System.out.print(yellow);
            System.out.print(guess.charAt(i));
        }
        System.out.print(reset);
    }

    private static String generateGuess(HashSet<String> originalWords, HashSet<String> possibleWords) {
        String bestGuess = "";
        String bestGuessForPossibleWords = "";
        double bestAlphaValue = Double.MAX_VALUE;
        double bestAlphaValueForPossibleWords = Double.MAX_VALUE;

        for (String guess : originalWords) {
            HashMap<String, Integer> patternToNumPosTargets = new HashMap<String, Integer>();
            for (String possibleTarget : possibleWords) {
                String pattern = findPattern(guess, possibleTarget, generateTargetMap(possibleTarget));
                patternToNumPosTargets.put(pattern, patternToNumPosTargets.getOrDefault(pattern, 0) + 1);
            }
            double alpha = findAlpha(patternToNumPosTargets);
            if (alpha < bestAlphaValue) {
                bestGuess = guess;
                bestAlphaValue = alpha;
            }
            if (possibleWords.contains(guess) && alpha < bestAlphaValueForPossibleWords) {
                bestGuessForPossibleWords = guess;
                bestAlphaValueForPossibleWords = alpha;
            }
        }
        return chooseBestGuess(bestGuessForPossibleWords, bestGuess, bestAlphaValueForPossibleWords, bestAlphaValue);

    }

    private static String chooseBestGuess(String targetSetGuess, String allWordsGuess, double targetAlpha,
            double allWordsAlpha) {
        if (Optimizer && targetAlpha > ratioForChange * allWordsAlpha) {
            alphaValueOfGuess = allWordsAlpha;
            return allWordsGuess;
        }
        alphaValueOfGuess = targetAlpha;
        return targetSetGuess;
    }

    private static HashSet<String> removeNAWords(HashSet<String> possibleWords, String guess, String realPattern) {
        HashSet<String> newSet = new HashSet<String>();
        for (String target : possibleWords) {
            String currentPattern = findPattern(guess, target, generateTargetMap(target));
            if (currentPattern.equals(realPattern))
                newSet.add(target);
        }
        return newSet;
    }

    private static HashSet<String> readWords(String filePath) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filePath));
        HashSet<String> originalWords = new HashSet<String>();
        while (sc.hasNext()) {
            String s = sc.next();
            originalWords.add(s);
        }
        return originalWords;
    }

    private static HashMap<Character, Integer> generateTargetMap(String target) {
        HashMap<Character, Integer> targetMap = new HashMap<>();
        for (char c : target.toCharArray()) {
            if (!targetMap.containsKey(c))
                targetMap.put(c, 0);
            targetMap.put(c, targetMap.get(c) + 1);
        }
        return targetMap;
    }

    // Optimise the findPattern function to speed up the first guess generation.
    private static String findPattern(String guess, String target, HashMap<Character, Integer> targetMap) {
        char[] targetArray = target.toCharArray();
        char[] patternArray = new char[5];
        for (int i = 0; i < guess.length(); i++) {
            char c = guess.charAt(i);
            if (c == targetArray[i]) {
                targetMap.put(c, targetMap.get(c) - 1);
                patternArray[i] = 'G';
                targetArray[i] = ' ';
            }
        }

        for (int i = 0; i < guess.length(); i++) {
            if (patternArray[i] == 'G')
                continue;
            char c = guess.charAt(i);
            int numLetters = targetMap.getOrDefault(c, 0);
            if (numLetters > 0) {
                patternArray[i] = 'Y';
                targetMap.put(c, numLetters - 1);
            } else
                patternArray[i] = 'B';

        }
        return new String(patternArray);
    }
}
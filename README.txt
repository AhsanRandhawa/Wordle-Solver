Abel, Ahsan, Victor
To compile this program, run the command: javac WordleSolver.java.

To run the program, use the following command: java WordleSolver [path/to/wordlewords.txt] [target_word].

The program can take two optional command line arguments. These are [Optimizer] and [AutoFirstGuess].
Enabling the [Optimizer] allows the wordlesolver to guess words that are not in the set of possible words but still 
give information about the target word. For example, in figuring out the final character. 

The [AutoFirstGuess] determines whether the first guess is determined at run time or is the precomputed value stored
in the program. Turning this on makes to program take longer to complete.

To run the program with [Optimizer] enabled, run the command: java WordleSolver filePath targetWord t. 
To run the program with [AutoFirstGuess] enabled, run the command: java WordleSolver filePath targetWord t t.
Note: t is used to enable [Optimizer] and [AutoFirstGuess], and any other character can be used to disable either option. 
package com.reginapeyfuss.services.higherOrderFunction

import org.scalatest.FunSuite

class WordCountTest extends FunSuite {

    test("splitting the sentence into words"){
        val input  = s"The Senate of the United States shall be composed of two Senators from each State, chosen by the Legislature thereof, for six Years; and each Senator shall have one " +
            s"Vote.\n\rImmediately after they shall be assembled in Consequence of the first Election, they shall be divided as equally as may be into three Classes. The Seats of the Senators of the first " +
            "Class shall be vacated at the " +
            "Expiration of the second Year, of the second Class at the Expiration of the fourth Year, and of the third Class at the Expiration of the sixth Year, so that one third may be chosen every second " +
            "Year; and if Vacancies happen by Resignation, or otherwise, during the Recess of the Legislature of any State, the Executive thereof may make temporary Appointments until the next Meeting of the Legislature, " +
            "which shall then fill such " +
            "Vacancies.\n\rNo Person shall be a Senator who shall not have attained to the Age of thirty Years, and been nine Years a Citizen of the United States, and who shall not, when elected, be an Inhabitant of that " +
            "State for which he shall be chosen.\n\rThe Vice President of the United States shall be President of the Senate, but shall have no Vote, unless they be equally divided.\n\rThe Senate shall chuse their " +
            "other Officers, and also a President pro tempore, in the Absence of the Vice President, or when he shall exercise the Office of President of the United States.\n\rThe Senate shall have the sole Power to " +
            "try all Impeachments. When sitting for that Purpose, they shall be on Oath or Affirmation. When the President of the United States is tried, the Chief Justice shall preside: And no Person shall be convicted " +
            "without the Concurrence of two thirds of the Members present.\n\rJudgment in Cases of Impeachment shall not extend further than to removal from Office, and disqualification to hold and enjoy any Office of honor, " +
            "Trust or Profit under the United States: but the Party convicted shall nevertheless be liable and subject to Indictment, Trial, Judgment and Punishment, according to Law.\n\rSection. 4."

        val words = WordCountManager.splitWords(input)
        assert(words.size == 349)
    }

    test("splitting the sentence into words with expected list check"){
        val sentence = s"The Senate of the United States shall be composed of two Senators from each State."
        val expectedList = List(
            "The", "Senate", "of", "the", "United", "States", "shall", "be", "composed", "of", "two", "Senators",
            "from", "each", "State."
        )
        val words = WordCountManager.splitWords(sentence)
        assert(words == expectedList)
    }

    test("finding longest word from sentence"){
        val sentence = s"The Senate of the United States shall be composed of two Senators from each State."
        val wordList = WordCountManager.splitWords(sentence)
        val longestWord = WordCountManager.findLongest(wordList)
        assert(longestWord == "Senators")
    }

    test("finding shortest word from sentence"){
        //this will take the last shortest word
        val sentence = s"The Senate of the United States shall be composed of two Senators from each State."
        val wordList = WordCountManager.splitWords(sentence)
        val longestWord = WordCountManager.findShortest(wordList)
        assert(longestWord == "of")
    }

    test("finding shortest word from sentence reduce left"){
        //this will take the last shortest word
        val sentence = s"The Senate of the United States shall be composed in two Senators from each State."
        val wordList = WordCountManager.splitWords(sentence)
        val longestWord = WordCountManager.findShortestLeft(wordList)
        assert(longestWord == "in")
    }

    test("finding shortest word from sentence reduce right"){
        //this will take the last shortest word
        val sentence = s"The Senate of the United States shall be composed in two Senators from each State."
        val wordList = WordCountManager.splitWords(sentence)
        val longestWord = WordCountManager.findShortestRight(wordList)
        assert(longestWord == "of")
    }

    test ("find word in list of words"){
        val sentence = s"The Senate of the United States shall be composed in two Senators from each State."
        val wordList = WordCountManager.splitWords(sentence)
        val foundWord = WordCountManager.findWord("of", wordList)
        assert(foundWord == "of")
    }

    test ("find no word in list of words"){
        val sentence = s"The Senate of the United States shall be composed in two Senators from each State."
        val wordList = WordCountManager.splitWords(sentence)
        val foundWord = WordCountManager.findWord("test", wordList)
        assert(foundWord == "")
    }

    test("distinct list of words") {
        val sentence = s"The Senate of the United States shall be composed of two Senators."
        val wordList = WordCountManager.splitWords(sentence)
        val foundWord = WordCountManager.uniqueWords(wordList)
        val expectedList = List("The", "Senate", "of", "the", "United", "States", "shall", "be", "composed", "two", "Senators.")
        assert(foundWord == expectedList)
    }

    test ("divide left") {
        val numbers = List(3.0, 4.0, 10.5)
        val division = WordCountManager.divideReduceLeft(numbers)
        assert(division == 0.07142857142857142)
    }

    test ("divide right") {
        val numbers = List(3.0, 4.0, 10.5)
        val division = WordCountManager.divideReduceRight(numbers)
        assert(division == 7.875)
    }
}

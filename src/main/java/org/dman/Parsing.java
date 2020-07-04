package org.dman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Parsing {

    private static final ArrayList<String> bag_of_words = new ArrayList<>(); // Мешок всех слов(стемминг)

    private static final ArrayList<String> document = new ArrayList<>(); // Все предложения

    private static final ArrayList<String> origin_document = new ArrayList<>(); // Все предложения

    public static ArrayList<String> getBag_of_words() {
        return bag_of_words;
    }

    public static ArrayList<String> getDocument() {
        return document;
    }

    public static ArrayList<String> getOrigin_document() {
        return origin_document;
    }

    public static String deleteCharacters(String token) { //Удаление символов.
        token = token.replaceAll("[!,'#$%&()*+./:;<=>?@_{|}~]", "");
        if (token.length() < 2)
            token = null;
        return token;
    }

    public static boolean isStopWords(String token) { //Проверка, является ли токен стоп-словом.
        String[] stopwords = {"the", "of", "and", "for", "a", "а", "е", "и", "ж", "м", "о", "на", "не", "ни", "об", "но", "он", "мне",
                "мои", "мож", "она", "они", "оно", "мной", "много", "многочисленное", "многочисленная",
                "многочисленные", "многочисленный", "мною", "мой", "мог", "могут", "можно", "может",
                "можхо", "мор", "моя", "моё", "мочь", "над", "нее", "оба", "нам", "нем", "нами", "ними",
                "мимо", "немного", "одной", "одного", "менее", "однажды", "однако", "меня", "нему",
                "меньше", "ней", "наверху", "него", "ниже", "мало", "надо", "один", "одиннадцать",
                "одиннадцатый", "назад", "наиболее", "недавно", "миллионов", "недалеко", "между", "низко",
                "меля", "нельзя", "нибудь", "непрерывно", "наконец", "никогда", "никуда", "нас", "наш", "нет",
                "нею", "неё", "них", "мира", "наша", "наше", "наши", "ничего", "начала", "нередко", "несколько",
                "обычно", "опять", "около", "мы", "ну", "нх", "от", "отовсюду", "особенно", "нужно", "очень", "отсюда",
                "в", "во", "вон", "вниз", "внизу", "вокруг", "вот", "восемнадцать", "восемнадцатый", "восемь",
                "восьмой", "вверх", "вам", "вами", "важное", "важная", "важные", "важный", "вдали", "везде", "ведь",
                "вас", "ваш", "ваша", "ваше", "ваши", "впрочем", "весь", "вдруг", "вы", "все", "второй", "всем",
                "всеми", "времени", "время", "всему", "всего", "всегда", "всех", "всею", "всю", "вся", "всё", "всюду",
                "г	год", "говорил", "говорит", "года", "году", "где", "да", "ее", "за", "из", "ли", "же", "им", "до",
                "по", "ими", "под", "иногда", "довольно", "именно", "долго", "позже", "более", "должно", "пожалуйста",
                "значит", "иметь", "больше", "пока", "ему", "имя", "пор", "пора", "потом", "потому", "после", "почему",
                "почти", "посреди", "ей", "два", "две", "двенадцать", "двенадцатый", "двадцать", "двадцатый", "двух",
                "его", "дел", "или", "без", "день", "занят", "занята", "занято", "заняты", "действительно", "давно",
                "девятнадцать", "девятнадцатый", "девять", "девятый", "даже", "алло", "жизнь", "далеко", "близко",
                "здесь", "дальше", "для", "лет", "зато", "даром", "первый", "перед", "затем", "зачем", "лишь", "десять",
                "десятый", "ею", "её", "их", "бы", "еще", "при", "был", "про", "процентов", "против", "просто",
                "бывает", "бывь", "если", "люди", "была", "были", "было", "будем", "будет", "будете", "будешь",
                "прекрасно", "буду", "будь", "будто", "будут", "ещё", "пятнадцать", "пятнадцатый", "друго", "другое",
                "другой", "другие", "другая", "других", "есть", "пять", "быть", "лучше", "пятый", "к", "ком", "конечно",
                "кому", "кого", "когда", "которой", "которого", "которая", "которые", "который", "которых", "кем",
                "каждое", "каждая", "каждые", "каждый", "кажется", "как", "какой", "какая", "кто", "кроме", "куда",
                "кругом", "с", "т", "у", "я", "та", "те", "уж", "со", "то", "том", "снова", "тому", "совсем", "того",
                "тогда", "тоже", "собой", "тобой", "собою", "тобою", "сначала", "только", "уметь", "тот", "тою",
                "хорошо", "хотеть", "хочешь", "хоть", "хотя", "свое", "свои", "твой", "своей", "своего", "своих",
                "свою", "твоя", "твоё", "раз", "уже", "сам", "там", "тем", "чем", "сама", "сами", "теми", "само",
                "рано", "самом", "самому", "самой", "самого", "семнадцать", "семнадцатый", "самим", "самими", "самих",
                "саму", "семь", "чему", "раньше", "сейчас", "чего", "сегодня", "себе", "тебе", "сеаой", "человек",
                "разве", "теперь", "себя", "тебя", "седьмой", "спасибо", "слишком", "так", "такое", "такой", "такие",
                "также", "такая", "сих", "тех", "чаще", "четвертый", "через", "часто", "шестой", "шестнадцать",
                "шестнадцатый", "шесть", "четыре", "четырнадцать", "четырнадцатый", "сколько", "сказал", "сказала",
                "сказать", "ту", "ты", "три", "эта", "эти", "что", "это", "чтоб", "этом", "этому", "этой", "этого",
                "чтобы", "этот", "стал", "туда", "этим", "этими", "рядом", "тринадцать", "тринадцатый", "этих",
                "третий", "тут", "эту", "суть", "чуть", "тысяч"};
        boolean stopword = false;
        for (String i:stopwords) {
            if (token.equals(i)) {
                stopword = true;
                break;
            }
        }
        return stopword;
    }

    public static boolean isNumeric(String token) { //Проверка, является ли токен числом.
        try {
            int number = Integer.parseInt(token);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String stemming(String token) {
        final Pattern PERFECTIVEGROUND = Pattern.compile("((\u0438\u0432|\u0438\u0432\u0448\u0438|\u0438\u0432\u0448\u0438\u0441\u044C|\u044B\u0432|\u044B\u0432\u0448\u0438|\u044B\u0432\u0448\u0438\u0441\u044C)|((?<=[\u0430\u044F])(\u0432|\u0432\u0448\u0438|\u0432\u0448\u0438\u0441\u044C)))$");
        final Pattern REFLEXIVE = Pattern.compile("(\u0441[\u044F\u044C])$");
        final Pattern ADJECTIVE = Pattern.compile("(\u0435\u0435|\u0438\u0435|\u044B\u0435|\u043E\u0435|\u0438\u043C\u0438|\u044B\u043C\u0438|\u0435\u0439|\u0438\u0439|\u044B\u0439|\u043E\u0439|\u0435\u043C|\u0438\u043C|\u044B\u043C|\u043E\u043C|\u0435\u0433\u043E|\u043E\u0433\u043E|\u0435\u043C\u0443|\u043E\u043C\u0443|\u0438\u0445|\u044B\u0445|\u0443\u044E|\u044E\u044E|\u0430\u044F|\u044F\u044F|\u043E\u044E|\u0435\u044E)$");
        final Pattern PARTICIPLE = Pattern.compile("((\u0438\u0432\u0448|\u044B\u0432\u0448|\u0443\u044E\u0449)|((?<=[\u0430\u044F])(\u0435\u043C|\u043D\u043D|\u0432\u0448|\u044E\u0449|\u0449)))$");
        final Pattern VERB = Pattern.compile("((\u0438\u043B\u0430|\u044B\u043B\u0430|\u0435\u043D\u0430|\u0435\u0439\u0442\u0435|\u0443\u0439\u0442\u0435|\u0438\u0442\u0435|\u0438\u043B\u0438|\u044B\u043B\u0438|\u0435\u0439|\u0443\u0439|\u0438\u043B|\u044B\u043B|\u0438\u043C|\u044B\u043C|\u0435\u043D|\u0438\u043B\u043E|\u044B\u043B\u043E|\u0435\u043D\u043E|\u044F\u0442|\u0443\u0435\u0442|\u0443\u044E\u0442|\u0438\u0442|\u044B\u0442|\u0435\u043D\u044B|\u0438\u0442\u044C|\u044B\u0442\u044C|\u0438\u0448\u044C|\u0443\u044E|\u044E)|((?<=[\u0430\u044F])(\u043B\u0430|\u043D\u0430|\u0435\u0442\u0435|\u0439\u0442\u0435|\u043B\u0438|\u0439|\u043B|\u0435\u043C|\u043D|\u043B\u043E|\u043D\u043E|\u0435\u0442|\u044E\u0442|\u043D\u044B|\u0442\u044C|\u0435\u0448\u044C|\u043D\u043D\u043E)))$");
        final Pattern NOUN = Pattern.compile("(\u0430|\u0435\u0432|\u043E\u0432|\u0438\u0435|\u044C\u0435|\u0435|\u0438\u044F\u043C\u0438|\u044F\u043C\u0438|\u0430\u043C\u0438|\u0435\u0438|\u0438\u0438|\u0438|\u0438\u0435\u0439|\u0435\u0439|\u043E\u0439|\u0438\u0439|\u0439|\u0438\u044F\u043C|\u044F\u043C|\u0438\u0435\u043C|\u0435\u043C|\u0430\u043C|\u043E\u043C|\u043E|\u0443|\u0430\u0445|\u0438\u044F\u0445|\u044F\u0445|\u044B|\u044C|\u0438\u044E|\u044C\u044E|\u044E|\u0438\u044F|\u044C\u044F|\u044F)$");
        final Pattern RVRE = Pattern.compile("^(.*?[\u0430\u0435\u0438\u043E\u0443\u044B\u044D\u044E\u044F])(.*)$");
        final Pattern DERIVATIONAL = Pattern.compile(".*[^\u0430\u0435\u0438\u043E\u0443\u044B\u044D\u044E\u044F]+[\u0430\u0435\u0438\u043E\u0443\u044B\u044D\u044E\u044F].*\u043E\u0441\u0442\u044C?$");
        final Pattern DER = Pattern.compile("\u043E\u0441\u0442\u044C?$");
        final Pattern SUPERLATIVE = Pattern.compile("(\u0435\u0439\u0448\u0435|\u0435\u0439\u0448)$");
        final Pattern I = Pattern.compile("\u0438$");
        final Pattern P = Pattern.compile("\u044C$");
        final Pattern NN = Pattern.compile("\u043D\u043D$");

        token = token.toLowerCase();
        token = token.replace('\u0451', '\u0435'); // ё, е
        Matcher m = RVRE.matcher(token);
        if (m.matches()) {
            String pre = m.group(1);
            String rv = m.group(2);
            String temp = PERFECTIVEGROUND.matcher(rv).replaceFirst("");
            if (temp.equals(rv)) {
                rv = REFLEXIVE.matcher(rv).replaceFirst("");
                temp = ADJECTIVE.matcher(rv).replaceFirst("");
                if (!temp.equals(rv)) {
                    rv = temp;
                    rv = PARTICIPLE.matcher(rv).replaceFirst("");
                } else {
                    temp = VERB.matcher(rv).replaceFirst("");
                    if (temp.equals(rv)) {
                        rv = NOUN.matcher(rv).replaceFirst("");
                    } else {
                        rv = temp;
                    }
                }
            } else {
                rv = temp;
            }

            rv = I.matcher(rv).replaceFirst("");

            if (DERIVATIONAL.matcher(rv).matches()) {
                rv = DER.matcher(rv).replaceFirst("");
            }

            temp = P.matcher(rv).replaceFirst("");
            if (temp.equals(rv)) {
                rv = SUPERLATIVE.matcher(rv).replaceFirst("");
                rv = NN.matcher(rv).replaceFirst("н");
            } else{
                rv = temp;
            }
            token = pre + rv;
        }
        return token;
    }

    public static void stringParsing(String text) { //Разбор предложения.
        ArrayList<String> bag_of_words_temporary = new ArrayList<>();
        Pattern sentencePattern = Pattern.compile(
                "# Поиск конца предложения по знакам пунктуации или по символу конца строки. \n" +
                        "[^.!?\\s]    # Первый символ - не знак пунктуации и не пробельный символ. \n" +
                        "[^.!?]*      # Любые символы, кроме трёх знаков пунктуации. \n" +
                        "(?:          # Группа для организации цикла. \n" +
                        "  [.!?]      # Особый случа: допустимы внутренние знаки пунктуации, если \n" +
                        "  (?!['\"]?\\s|$)  # далее не следуют пробельные символы или конец строки. \n" +
                        "  [^.!?]*    # Любые символы, кроме трёх знаков пунктуации. \n" +
                        ")*           # Любое число таких групп, включая нулевое (звезда клини) \n" +
                        "[.!?]?       # Необязательный конечный знак пунктуации (один). \n" +
                        "['\"]?       # Необязятельная закрывающая кавычка (одиночная или двойная). \n" +
                        "(?=\\s|$)",
                Pattern.MULTILINE | Pattern.COMMENTS);
        Matcher matcher = sentencePattern.matcher(text);
        while (matcher.find()) {
            String sentence_buffer = "";
            String text_buffer = "";
            Scanner scanner = new Scanner(matcher.group());
            while (scanner.hasNext()) {
                String token = scanner.next();
                text_buffer += token + " ";
                token = deleteCharacters(token); //Удаляем символы.
                if (token != null)
                    if (!isStopWords(token.toLowerCase()) & !isNumeric(token)) { //Если слово - не стоп-слово и не число, то
                        bag_of_words_temporary.add(stemming(token));
                        sentence_buffer += stemming(token) + " ";
                    }
            }
            origin_document.add(text_buffer);
            document.add(sentence_buffer);
        }
        for (String s: bag_of_words_temporary) {
            if (Collections.frequency(bag_of_words_temporary, s) >= 2)
                if (!bag_of_words.contains(s) & bag_of_words.size() < 11) {
                    bag_of_words.add(s);
                }
        }
    }

    public static ArrayList<String> readTxt(String path) throws IOException {
        String str, text = "";
        BufferedReader reader = new BufferedReader(new FileReader(path));
        while ((str = reader.readLine()) != null)
            text += str + " ";
        stringParsing(text);
        return document;
    }

    public static String Keywords_To_List(String input) {
        String keywords = "";
        Scanner scanner = new Scanner(input.toLowerCase());
        while (scanner.hasNext()) {
            String token = deleteCharacters(scanner.next()); // Удаляем символы.
            if (token != null)
                if (!isStopWords(token) & !isNumeric(token)) // Если слово - не стоп-слово и не число, то
                    keywords+=stemming(token) + " ";
        }
        return keywords;
    }
}

/*
 * Class:       CS 4308 Section 2
 * Term:        Spring 2019
 * Name:        Jackson Emery
 * Instructor:   Sharon Perry
 * Project:  Deliverable 1 Scanner - Java
 */

import java.util.*;
import java.util.Dictionary;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Scanner
{

  public static String KeywordsTable[] = {"end", "else", "catch", "finally", "true",
  "false", "begin", "while", "if", "for", "try", "return", "break", "continue",
  "function", "macro", "quote", "let", "local", "global", "const", "do", "struct",
  "abstract", "typealias", "bitstype", "type", "immutable", "module", "baremodule",
  "using", "import", "export", "importall"};

  public static char Digits[] = {'0','1','2','3','4','5','6','7','8','9'};

  public static void main(String[] args)
  {

    String input = fileToString("/Users/echelon/Desktop/Projects/testJulia1.txt");

    if(input.length() < 1) {
            System.out.println("No input");
            return;
        }
        String output = "";
        List<Token> tokens = JavaScanner(input);
        for(Token t : tokens) {
            output += "Next token is " + t.getID() + " Next lexeme is " + t.getLex() + " | ";
        }
        System.out.println(output);

        try {
          usingFileWriter(output);
        }
        catch(Exception e){}
  }

  public static enum Type
  {
    KEYWORDS, LPAREN, RPAREN, ASSIGNMENT_OPERATOR, LETTERS, DIGITS,
    LE_OPERATOR, LT_OPERATOR, GE_OPERATOR, GT_OPERTOR, EQ_OPERATOR,
    NE_OPERATOR, ADD_OPERATOR, SUB_OPERATOR, MUL_OPERATOR, DIV_OPERATOR,
    POW_OPERATOR, COLON, COMMA, PERIOD;
  };

  public static class Token
  {
    public final Type t;
    public final String c;
    public final int id;

    public Token (Type t, String c, int id)
    {
      this.t = t;
      this.c = c;
      this.id = id;
    }

    public int getID()
    {
      return id;
    }

    public String getLex()
    {
      return c;
    }

    public Type getType()
    {
      return t;
    }
  }

  public static String getLexeme(String s, int i)
  {
    int j = i;
    for(; j<s.length(); )
    {
      if(Character.isLetter(s.charAt(j)))
      {
        j++;
      }
      else if(Character.isDigit(s.charAt(j)) && !Character.isLetter(s.charAt(j)))
      {
        j++;
      }
      else
      {
        return s.substring(i,j);
      }
    }
    return s.substring(i,j);
  }

  public static boolean isInteger(String input)
  {
    for(int i = 0; i < input.length();)
    {
      if(Character.isDigit(input.charAt(i)))
      {
        i++;
      }
      else
      {
        return false;
      }
    }
    return true;
  }

  public static int grabNumeric(String keyword)
  {
    for(int j = 0; j < KeywordsTable.length; j++)
    {
      if(KeywordsTable[j].equals(keyword))
      {
        return j;
      }
    }
    return 0;
  }

  public static List<Token> JavaScanner(String input)
  {
    List<Token> result = new ArrayList<Token>();
    for(int i = 0; i < input.length();)
    {
      switch(input.charAt(i))
      {
        case '(':
          result.add(new Token(Type.LPAREN, "(", 34));
          i++;
          break;

        case ')':
          result.add(new Token(Type.RPAREN, ")", 35));
          i++;
          break;

        case '=':
          i++;
          if(input.charAt(i) == '=')
          {
            result.add(new Token(Type.EQ_OPERATOR, "==", 36));
            i++;
            break;
          }
          i--;
          result.add(new Token(Type.ASSIGNMENT_OPERATOR, "=", 37));
          i++;
          break;

        case '<':
          i++;
          if(input.charAt(i) == '=')
          {
            result.add(new Token(Type.LE_OPERATOR, "<=", 40));
            i++;
            break;
          }
          i--;
          result.add(new Token(Type.LT_OPERATOR, "<", 41));
          i++;
          break;

        case '>':
          i++;
          if(input.charAt(i) == '=')
          {
            result.add(new Token(Type.LE_OPERATOR, ">=", 42));
            i++;
            break;
          }
          i--;
          result.add(new Token(Type.LT_OPERATOR, ">", 43));
          i++;
          break;

        case '+':
          i++;
          result.add(new Token(Type.ADD_OPERATOR, "+", 45));
          break;

        case '/':
          i++;
          result.add(new Token(Type.DIV_OPERATOR, "/", 46));
          break;

        case '-':
          i++;
          result.add(new Token(Type.SUB_OPERATOR, "-", 47));
          break;

        case '*':
          i++;
          result.add(new Token(Type.MUL_OPERATOR, "*", 48));
          break;

        case '~':
          i++;
          if(input.charAt(i) == '=')
          {
            result.add(new Token(Type.NE_OPERATOR, "~=",49));
            break;
          }
          i--;
          break;

        case '^':
          i++;
          result.add(new Token(Type.POW_OPERATOR, "^", 50));
          break;

        case ':':
          i++;
          result.add(new Token(Type.COLON, ":", 51));
          break;

        case ',':
          i++;
          result.add(new Token(Type.COMMA, ",", 52));
          break;

          case '.':
            i++;
            result.add(new Token(Type.PERIOD, ".", 53));
            break;

        default:
          if(Character.isWhitespace(input.charAt(i)))
          {
            i++;
          }
          String lexeme = getLexeme(input, i);
          i += lexeme.length();
          // if empty space, skip
          if(lexeme.equals(""))
          {
            break;
          }
          // if Atom is a keyword
          else if(Arrays.asList(KeywordsTable).contains(lexeme))
          {
            result.add(new Token(Type.KEYWORDS, lexeme, grabNumeric(lexeme)));
          }
          else if(isInteger(lexeme))
          {
            result.add(new Token(Type.DIGITS, lexeme, 39));
          }
          else
          // if atom is a word
          {
            result.add(new Token(Type.LETTERS, lexeme, 38));
          }
          break;
      }
    }
    return result;
  }

  public static String fileToString(String file)
  {
    String content = "";
    try{
      content = new String(Files.readAllBytes(Paths.get(file)), "UTF-8");
    }
    catch(Exception e) {
      return content;
    }
    return content;
  }

  public static void usingFileWriter(String fileContent) throws IOException
  {
      try{
        FileWriter fileWriter = new FileWriter("/Users/echelon/Desktop/ScannerOutput.txt");
        fileWriter.write(fileContent);
        fileWriter.close();
      }
      catch(Exception e) {
      }
  }
}

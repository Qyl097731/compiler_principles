package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class lex {

	private boolean flag = false;
	private int pos;
	// 单词符号类别定义，与实验的表格要求一致

	public enum symbol {
		period(".", 0), plus("+", 1), minus("-", 2), times("*", 3), slash("/", 4), eql("=", 5), neq("<>", 6), lss("<",
				7), leq("<=", 8), gtr(">", 9), geq(">=",
						10), lparen("(", 11), rparen(")", 12), semicolon(";", 13), becomes(":=", 14), beginsym("begin",
								15), endsym("end", 16), ifsym("if", 17), thensym("then", 18), whilesym("while",
										19), dosym("do", 20), ident("IDENT", 21), number("number", 22), nil("nil", 23),pequl("+=",24),mequl("-=",25),increment("++",26),decrement("--",27),multiequl("*=",28),slashequl("/=",29),equal("==",30),annobegin("/*",31),annoend("*/",32);

		private String strName;
		private int iIndex;
		private symbol(String name, int index) {
			this.strName = name;
			this.iIndex = index;
		}

		public String getStrName() {
			return this.strName;
		}

		public int getIIndex() {
			return this.iIndex;
		}
	};

	// 单词类定义二元组:（单词类别, 单词名）
	public class aWord {
		String name;
		symbol symtype;
		private aWord(String name, symbol symtype) {
			this.name = name;
			this.symtype = symtype;
		}

		public String toString() {
			return "(" + this.symtype.iIndex + "," + this.name.trim() + ")";
		}
	}

	/***************************************************************************
	 * 变量说明: line 从终端读入的字符串; 当前所指位置在计数器 iCurPos, 字符为ch token 正在识别的单词字符串；当前所指位置在计数器
	 * iIndex sym 每个单词符号种类，来源于symbol, 例：symbol.number keyword 保留字表 , 包括:begin, do,
	 * end , if, then, while(已排好序）
	 * 
	 * Symlist 识别出的符号表，每个元素是一个单词的二元组（sym, token)
	 ***************************************************************************/
	String line;
	int iCurPos = 0;
	char[] token;
	int iIndex = 0;
	symbol sym;
	String[] keyword = { "begin", "do", "end", "if", "then", "while" };
	ArrayList<aWord> Symlist;

	// 从终端读入一行程序
	public String getProgram() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		line = "";
		try {
			do {
				System.out.print("请读入程序串，以. 结束 ：");
				line = in.readLine();
			} while (line.endsWith(".") == false);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return line;
	}

	// 增加一个单词到符号表里
	public void AddaWordtoList() {
		aWord aWord;
		aWord = new aWord((new String(token)).trim(), sym);
		Symlist.add(aWord);
	}

	// Main函数：主入口
	public static void main(String[] args) {

		lex lex = new lex();

		// 读入一行程序串，以.结束
		lex.line = lex.getProgram();
		lex.Symlist = new ArrayList<aWord>(20);
		lex.Symlist.clear();

		// 主要操作：识别程序串line中的单词，并输出到符号表Symlist中去。
		while ((lex.iCurPos < lex.line.length())) {
			lex.getSym();
			// 把单词加到符号表中去
			if(!lex.flag)
				lex.AddaWordtoList();
		}

		// 输出单词符号表
		for (int i = 0; i < lex.Symlist.size(); i++) {
			System.out.print(lex.Symlist.get(i).toString().trim() + " ");
		}
	}

	/*******************
	 * 功能函数getSym：识别一个单词符号*********************************
	 * 
	 * 输入：line, iCurpos，当前程序串所指向位置。 例：line[]="begin x:=9 end." iCurpos=6时，则当前从x开始识别
	 * 
	 * 输出：(单词名token,单词类别sym)，并加入到符号表Symlist中。iCurpos更新到新的位置。
	 * 例：x识别后，Symlist增加("x",21), 同时更新iCurpos=7，为下一次识别作准备。
	 * 
	 *************************************************************************************/

	public void getSym() {
		iIndex = 0;
		flag = false;
		token = new char[100];
		sym = symbol.nil;
		int boundry = line.length();
		char ch = line.charAt(iCurPos++);
		/*********************************************************
		 *
		 *************** TODO: 单词识别 ***************
		 *
		 * 只需要做的是：针对不同的字符识别出相应的 token 和 sym 值
		 *
		 *********************************************************/
		while (ch == ' ' && iCurPos < boundry) {
			ch = line.charAt(iCurPos++);
		}

//		数字
		if (ch >= '0' && ch <= '9') {
			token[iIndex++] = ch;
			ch = line.charAt(iCurPos++);
//			扫数字
			while (ch >= '0' && ch <= '9' && iCurPos < boundry) {
				token[iIndex++] = ch;
				ch = line.charAt(iCurPos++);
			}
//			判别数字 后面跟的符号
//			是数字
			if (ch == ' ' || ch == ')' || ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == ':' || ch == '<'
					|| ch == ';' || ch == '>' || ch == '=' ) {
				sym = symbol.number;
			}
			//			非法数字 由 ） ;  ' ' 分割
			else {
				while (!(ch == ' ' || ch == ')' || ch == ';') && iCurPos < boundry) {
					token[iIndex++] = ch;
					ch = line.charAt(iCurPos++);
				}
			}
			iCurPos--;
		}
//		字符  字符或者下划线开始 由数字下划线字母组成
		else if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch =='_') {
			boolean mark = false;
			symbol[] vals = symbol.values();

			token[iIndex++] = ch;
			ch = line.charAt(iCurPos++);
			while (ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch == '_' && iCurPos < boundry) {
				token[iIndex++] = ch;
				ch = line.charAt(iCurPos++);

			}

			// 判断关键字
			for (int i = 0; i < vals.length; i++) {
				if (new String(token).trim().equals(vals[i].getStrName())) {
					mark = true;
					sym = vals[i];
					break;
				}
			}
			// 当前是一个标识符
			if (!mark && (ch == ')' || ch == ' ' || ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == ':'
					|| ch == '<' || ch == ';' || ch == '>' || ch == '=')) {
				sym = symbol.ident;
			}
//			非法的标识符 由 ） ;  ' ' 分割
			else {
				while (!(ch == ' ' || ch == ')' || ch == ';') && iCurPos < boundry) {
					token[iIndex++] = ch;
					ch = line.charAt(iCurPos++);
				}
			}

			iCurPos--;
		} else
			switch (ch) {
			case '.':
				token[iIndex++] = ch;
				sym = symbol.period;
				break;
			case '+':
				token[iIndex++] = ch;
				ch = line.charAt(iCurPos);
//				+=
				if(ch == '='){
					token[iIndex++] = ch;
					sym = symbol.pequl;
					iCurPos++;
//				++
				}else if(ch == '+'){
					token[iIndex++] = ch;
					sym = symbol.increment;
					iCurPos++;
				}
//				+
				else{
					sym = symbol.plus;
				}
				break;
			case '-':
				token[iIndex++] = ch;
//				-=
				if(ch == '='){
					token[iIndex++] = ch;
					sym = symbol.mequl;
					iCurPos++;
				}
//				--
				else if(ch == '-'){
					token[iIndex++] = ch;
					sym = symbol.decrement;
					iCurPos++;
				}
//				-
				else {
					sym = symbol.minus;
				}
				break;
			case '*':
				token[iIndex++] = ch;
				ch = line.charAt(iCurPos);
//				*=
				if(ch == '='){
					token[iIndex++] = ch;
					sym = symbol.multiequl;
					iCurPos++;
				}
//				*
				else {
					sym = symbol.times;
				}
				break;
			case '/':
/*			pos 如果匹配失败 不是注释 需要返回到 / 位置往后正常匹配
* 			flag 如果是注释 后面不会报错 也不会输出东西
*
 */
				pos = iCurPos;
				flag = false;
				token[iIndex++] = ch;
				ch = line.charAt(iCurPos);
//				/=
				if(ch == '='){
					token[iIndex++] = ch;
					sym = symbol.slashequl;
					iCurPos++;
				}
//				注释开始标志
				else if(ch == '*'){
					iCurPos++;
					while(iCurPos < boundry){
						ch = line.charAt(iCurPos);
//						如果 */ 出现证明匹配成功
						if(iCurPos + 1 < boundry && ch == '*' && line.charAt(iCurPos+1) == '/'){
							flag = true;
							break;
						}
						iCurPos++;
					}
//						 a a_+=b10 /* a_ = a_ + b10 * .  (有前缀空格)
//					 	 a a_+=b10 /* a_ = a_ + b10 */ .
//					不是注释
					if(!flag){
						iCurPos = pos;
						sym = symbol.slash;
					}else{
						iCurPos += 2;
					}
				}else{
					sym = symbol.slash;
				}
				break;
			case '=':
				token[iIndex++] = ch;
				ch = line.charAt(iCurPos);
//				==
				if(ch == '='){
					token[iIndex++] = ch;
					sym = symbol.equal;
					iCurPos++;
				}
//				=
				else {
					sym = symbol.eql;
				}
				break;
			case ';':
				token[iIndex++] = ch;
				sym = symbol.semicolon;
				break;
			case '(':
				token[iIndex++] = ch;
				sym = symbol.lparen;
				break;
			case ')':
				token[iIndex++] = ch;
				sym = symbol.rparen;
				break;
			case '<':
				token[iIndex++] = ch;
				ch = line.charAt(iCurPos++);
				if (ch == '>') {
					token[iIndex++] = ch;
					sym = symbol.neq;
				} else if (ch == '=') {
					token[iIndex++] = ch;
					sym = symbol.leq;
				} else {
					sym = symbol.eql;
					iCurPos--;
				}

				break;
			case '>':
				token[iIndex++] = ch;
				ch = line.charAt(iCurPos++);
				if (ch == '=') {
					token[iIndex++] = ch;
					sym = symbol.geq;
				} else {
					sym = symbol.gtr;
					iCurPos--;
				}
				break;
			case ':':
				token[iIndex++] = ch;
				ch = line.charAt(iCurPos++);
				if (ch == '=') {
					token[iIndex++] = ch;
					sym = symbol.becomes;
				} else {
					while (!(ch == ' ' || ch == ')' || ch == ';') && iCurPos < boundry) {
						token[iIndex++] = ch;
						ch = line.charAt(iCurPos++);
					}
				}
				break;
			default:
				token[iIndex++] = ch;
			}
		if (sym == symbol.nil && ch != ' ' && !flag) {
			System.out.println("Error: Position " + iCurPos + " occur the unexpected char \'" + ch + "\'.");
		}

	}
}
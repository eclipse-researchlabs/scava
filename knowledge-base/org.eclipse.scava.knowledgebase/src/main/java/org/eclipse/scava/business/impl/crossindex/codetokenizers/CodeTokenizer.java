package org.eclipse.scava.business.impl.crossindex.codetokenizers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;


public class CodeTokenizer {
	
	private static Set<String> javaTypes;
	private static Set<String> javaScriptTypes;
	private static Set<String> cTypes;
	private static Set<String> phpTypes;
	
	static
	{
		//These are the names of tokens' types that allow us to get the most relevant ones
		javaTypes = new HashSet<String>(1);
		javaTypes.add("IDENTIFIER");
		
		phpTypes = new HashSet<String>(1);
		phpTypes.add("Identifier");
		
		javaScriptTypes = new HashSet<String>(1);
		javaScriptTypes.add("Identifier");
		
		cTypes = new HashSet<String>(9);
		cTypes.add("Identifier");
		cTypes.add("Int");
		cTypes.add("Double");
		cTypes.add("Long");
		cTypes.add("Char");
		cTypes.add("Float");
		cTypes.add("Short");
		cTypes.add("Signed");
		cTypes.add("Bool");
	}
	
	/**
	 * This method will tokenize the program and get all the elements that are
	 * variable names, variables types, methods names. Depending on the library
	 * it will imported libraries names too.
	 * @param program
	 * @param language
	 * @param keepOrder If false, then the program's tokens will be filtered using multiple threads
	 * @return 
	 */
	public static Set<String> tokenize(String program, String language, boolean keepOrder)
	{
		CodePointCharStream code = CharStreams.fromString(program);
		Set<String> filteresTokens;
		switch(language)
		{
			case "c": 
				filteresTokens=CTokens(code, keepOrder);
				break;
			case "java":
				filteresTokens=JavaTokens(code, keepOrder);
				break;
			case "javascript":
				filteresTokens=JavaScriptTokens(code, keepOrder);
				break;
			case "php":
				filteresTokens=PhpTokens(code, keepOrder);
				break;
			default:
				System.err.println("Not supported language");
				filteresTokens=new HashSet<String>(0);
				break;
		}
		return filteresTokens;
	}
	
	private static Set<String> JavaTokens(CodePointCharStream code, boolean keepOrder)
	{
		JavaLexer lexer = new JavaLexer(code);
		return filterTokens(lexer, javaTypes, keepOrder);
	}
	
	private static Set<String> CTokens(CodePointCharStream code, boolean keepOrder)
	{
		CLexer lexer = new CLexer(code);
		return filterTokens(lexer, cTypes, keepOrder);
	}
	
	private static Set<String> PhpTokens(CodePointCharStream code, boolean keepOrder)
	{
		PhpLexer lexer = new PhpLexer(code);
		return filterTokens(lexer, phpTypes, keepOrder);
	}
	
	private static Set<String> JavaScriptTokens(CodePointCharStream code, boolean keepOrder)
	{
		JavaScriptLexer lexer = new JavaScriptLexer(code);
		return filterTokens(lexer, javaScriptTypes, keepOrder);
	}
	
	private static Set<String> filterTokens(Lexer lexer, Set<String> searchedTokensTypes, boolean keepOrder)
	{
		Vocabulary vocabulary = lexer.getVocabulary();
		List<Token> tokenized = tokenize(lexer);
		if(keepOrder)
		{
			Set<String> filteredTokens = new HashSet<String>();
			for(Token token : tokenized)
			{
				if(searchedTokensTypes.contains(vocabulary.getSymbolicName(token.getType())))
				{
					filteredTokens.add(token.getText());
				}
			}
			return filteredTokens;
		}
		else {
			Set<String> filteredTokens = Collections.synchronizedSet(new HashSet<String>());
			
			tokenized.parallelStream().forEach(t->{
				if(searchedTokensTypes.contains(vocabulary.getSymbolicName(t.getType())))
				{
					filteredTokens.add(t.getText());
				}
			});
			return filteredTokens;
		}
	}
	
	private static List<Token> tokenize(Lexer lexer)
	{
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();
		return tokens.getTokens();
	}
	
}

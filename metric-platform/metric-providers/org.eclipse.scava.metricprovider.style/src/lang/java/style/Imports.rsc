@license{
  Copyright (c) 2017 Centrum Wiskunde & Informatica
  
  This program and the accompanying materials are made
  available under the terms of the Eclipse Public License 2.0
  which is available at https://www.eclipse.org/legal/epl-2.0/
  
  SPDX-License-Identifier: EPL-2.0
}
module lang::java::style::Imports

import analysis::m3::Core;
import lang::java::m3::Core;
import lang::java::m3::AST;
import Message;
import String;
import List;

import lang::java::jdt::m3::Core;		// Java specific modules
import lang::java::jdt::m3::AST;

import lang::java::style::Utils;

import IO;

data Message = imports(str category, loc pos);

/*
AvoidStarImports	DONE
AvoidStaticImport	DONE
IllegalImport		DONE
RedundantImport		DONE
UnusedImports		TBD
ImportOrder			TBD
ImportControl		TBD
*/

/* --- avoidStarImport ------------------------------------------------------*/

list[Message] avoidStarImport(Declaration imp: \import(_),  list[Declaration] parents, M3 model) =
	\onDemand() in (imp.modifiers?{}) ? [imports("AvoidStarImport", imp.src)] : [];

default list[Message] avoidStarImport(Declaration imp,  list[Declaration] parents, M3 model) = [];

// avoidStaticImport

list[Message] avoidStaticImport(Declaration imp: \import(_),  list[Declaration] parents, M3 model) =
	\static() in (imp.modifiers?{}) ? [ imports("AvoidStaticImport", imp.src)] : [];
	
default list[Message] avoidStaticImport(Declaration imp,  list[Declaration] parents, M3 model) = [];	

/* --- illegalImport --------------------------------------------------------*/

list[Message] illegalImport(Declaration imp: \import(str name),  list[Declaration] parents, M3 model) =
	startsWith(name, "sun") ? [imports("IllegalImport", imp.src)] : [];

list[Message] illegalImport(Declaration imp,  list[Declaration] parents, M3 model) = [];

// TODO:

//list[Message] redundantImport(node ast, M3 model, OuterDeclarations decls) {
//	msgs = [];
//	
//	packageName = getPackageName(ast);
//   
//	imported = [ imp | /Declaration imp: \import(str name) := ast];
//	
//	for(imp: \import(str name) <- imported){
//		if(indexOf(imported, name) != lastIndexOf(imported, name)){
//			msgs += imports("UnusedImports", imp.src);
//		} else
//		if(startsWith(name, "java.lang")){
//			msgs += imports("UnusedImports", imp.src);
//		} else
//		if(startsWith(name, packageName)){
//			msgs += imports("UnusedImports", imp.src);
//		}
//	}
//	
//	return msgs;
//}

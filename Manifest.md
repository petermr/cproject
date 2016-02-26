# Manifests

<i>Still under development</i>

## architecture

Each directory (CProject or CTree) should have a manifest file (manifest.xml) which describes the contents of the directory, and possibly its history. Each manifest should conform to an XML specification (manifest template) which controls what files can/not be present in the directories. There are
two templates (currently only one version of each but it's possible they might vary between CProjects in the future). 

 * `cProjectTemplate.xml`. This applies to the CProject directory
 * `cTreeTemplate.xml`. This single template applies to every `CTree` in the project. Its object is stored in the CProject, which means that every `CTree` must know what its `CProject` is.
 
The template files are currently in `src/main/resources` with resource String `/org/xmlcml/files` and are created/loaded by default.

## template syntax

Template elements and attributes include:

 * `p` . documentation in HTML for the parent element.
 * `cProject`. Specification for a `CProject`
 * `cTree`. Specification for a `CTree`
 * `@name` indicates a hardcoded name or a regular expression.
 * `@count="n,m"` indicates how many times a file or directory may occur. counts are inclusive and `n` <= `m` (the name must be a regex)
 * `file`. A file, usually with a hardcoded name, or a hardcoded syntax.
 * `dir`. A directory, usually with a hardcoded name, or a hardcoded syntax. 
 * `<dir type="CTree"` is a special case and indicates a child `CTree` in a `CProject`. The name could be anything
 * `<dir type="plugin"` is a special case and indicates a plugin . 
 

```
<cProject>
	<p>CProject file container. </p>
	<file name="manifest.xml" count="1,1"/>
	<file name="log.xml"  count="0,1"//>
	<dir type="CTree" count="0,9999999999">
  	  <file name="manifest.xml" count="1,1"/>
	</dir>
	<file name="eupmc_results.json" count="0,1"/>
	<dir name="results" count="0,1"/>
</cProject>
```
or
```
<cTree>
	<dir type="image">
	  <dir name="image_\d+">
	    <file name="image.gif" count="0,1"/>
	    <file name="image.png" count="0,1"/>
	    <file name="image.svg" count="0,1"/>
	  </dir>
	</dir>
	<dir name="results">
	  <dir type="plugin" count="*">
  	    <dir type="plugin" count="*">
	      <file name="results.xml" count="0,1"/>
	      <file name="results.html" count="0,1"/>
	    </dir>
	  </dir>
	</dir>
	<dir name="suppData">
	  <dir name="data_\d+">
	    <file name="data.csv" count="0,1"/>
	    <file name="data.txt" count="0,1"/>
	    <file name="data.xml" count="0,1"/>
	  </dir>
	</dir>
</cTree>
```

## `cProjectTemplate.xml` 


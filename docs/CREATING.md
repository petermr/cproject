# Creating CMDirs

(See also CREATING_CM.md in `norma` - this may need to be rationalised)

## from single file

see CMDirTest.testCreateCMDir()

```
norma -i src/test/resources/org/xmlcml/files/misc/test_pdf_1471-2148-14-70.pdf  -o target/testcreate/ --cmdir
```
This will create a new `CMDir` in ```target/testcreate/``` and copy 
```src/test/resources/org/xmlcml/files/misc/test_pdf_1471-2148-14-70.pdf``` to 
```target/testcreate/fulltext.pdf```

## from multiple files

```
norma -i src/test/resources/org/xmlcml/files/misc/theses/ -e pdf -o target/testcreate/theses/ --cmdir";
```
will make a list of all the ```*.pdf``` in ```src/test/resources/org/xmlcml/files/misc/theses/``` and
create a new ```CMDir``` for each including ```fulltext.pdf```.




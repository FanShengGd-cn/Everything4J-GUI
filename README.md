# Everything4J-GUI

----

Provide a Listary-like gui interface, it could search all the files by file name on Windows NTFS
This project is based on Everything4J
Everything4J Link： https://github.com/Xarrow/Everything4J

----

# Dependencies
Everything4J basic dependencies
Additional:
```xml
<dependency>
    <groupId>com.melloware</groupId>
    <artifactId>jintellitype</artifactId>
    <version>1.4.1</version>
</dependency>
```


```

----

# Usage

1. compile as jar

```
mvn clean install -Dmaven.test.skip=true
```

2. import target jar into project.

3. put dynamic link library (Everything32.dll , Everything64.dll) into the root of your project.

4. search full file path according to search key string.

```java

private static Everything4j everything4jInstance = Everything4j.getInstance();

List<String> sl = everything4jInstance.searchResult("abc");
System.out.

println(sl.size());
        for(
int i = 0; i <sl.

size();

i++){
        System.out.

println(i +" ==> "+sl.get(i));
        }
        }
```

----

# License

Apache 2

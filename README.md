github资源文件目录结构为

> hibernate   
> .gitignore   
> LICENSE     
> README       
> pom.xml       

1、将pom.xml里的&lt;modules&gt;下的"&lt;module&gt;hibernate&lt;/module&gt;"粘贴到pinpoint\plugins\pom.xml相同的&lt;modules&gt;里，将

    <dependency>
      <groupId>com.navercorp.pinpoint</groupId>
      <artifactId>pinpoint-hibernate-plugin</artifactId>
      <version>${project.version}</version>
    </dependency>
    
粘贴到pinpoint\plugins\pom.xml里                                    
2、将hibernate文件夹复制到pinpoint\plugins\下                                    
3、修改所有pom的版本                                    
4、重新导入项目                                  

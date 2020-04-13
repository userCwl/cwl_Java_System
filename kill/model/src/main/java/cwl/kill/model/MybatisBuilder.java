package cwl.kill.model;
/**
 * Mybatis逆向工程
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
public class MybatisBuilder {
    public void generator() throws Exception{
        // warnings 为用于放置生成过程中警告信息的集合对象
        List<String> warnings = new ArrayList<String>();
        // 指定是否覆盖重名文件
        boolean overwrite = true;
        //加载配置文件
        String path = this.getClass().getClassLoader().getResource("./genreatorConfig.xml").getPath();
        File configFile = new File(path);
        // 配置解析类
        ConfigurationParser cp = new ConfigurationParser(warnings);
        // 配置解析类解析配置文件并生成 Configuration 配置对象
        Configuration config = cp.parseConfiguration(configFile);
        // ShellCallback 负责如何处理重复文件
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        // 逆向工程对象
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        // 执行逆向文件生成操作
        myBatisGenerator.generate(null);
        // 打印提示信息
        System.out.println("MyBatis 逆向工程执行成功，刷新项目查看文件！");
    }
    public static void main(String[] args) {
        try {
            new MybatisBuilder().generator();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


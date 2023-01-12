package net.lanternmc.plugins;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.logging.Logger;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.representer.Representer;

public class FileUtil extends YamlConfiguration {
   protected final DumperOptions yamlOptions;
   protected final Representer yamlRepresenter;
   protected final Yaml yaml;
   protected File file;
   protected Logger loger;
   protected Plugin plugin;

   public FileUtil(Plugin plugin) {
      this(plugin, "plugins.yml");
   }

   public FileUtil(Plugin plugin, File file) {
      this.yamlOptions = new DumperOptions();
      this.yamlRepresenter = new YamlRepresenter();
      this.yaml = new Yaml(new YamlConstructor(), this.yamlRepresenter, this.yamlOptions);
      Validate.notNull(file, "File cannot be null");
      Validate.notNull(plugin, "Plugin cannot be null");
      this.plugin = plugin;
      this.file = file;
      this.loger = plugin.getLogger();
      this.check(file);
      this.init(file);
   }

   public FileUtil(Plugin plugin, String filename) {
      this(plugin, new File(plugin.getDataFolder(), filename));
   }

   private void check(File file) {
      String filename = file.getName();
      InputStream stream = this.plugin.getResource(filename);

      try {
         if (!file.exists()) {
            if (!file.getParentFile().exists()) {
               file.getParentFile().mkdirs();
            }

            file.createNewFile();
            if (stream != null) {
               this.plugin.saveResource(filename, true);
//               List<String> lobbys = new ArrayList();
//               lobbys.add("lobby");
//               FindLantern.getMe().getConfig().set("lobby", lobbys);
            }
         }
      } catch (IOException var5) {
         this.loger.info("配置文件 " + filename + " 创建失败...");
      }

   }

   private void init(File file) {
      Validate.notNull(file, "File cannot be null");

      try {
         FileInputStream stream = new FileInputStream(file);
         this.init((InputStream)stream);
      } catch (FileNotFoundException var3) {
         this.loger.info("配置文件 " + file.getName() + " 不存在...");
      }

   }

   private void init(InputStream stream) {
      Validate.notNull(stream, "Stream cannot be null");

      try {
         this.load((Reader)(new InputStreamReader(stream, Charsets.UTF_8)));
      } catch (IOException var3) {
         this.loger.info("配置文件 " + this.file.getName() + " 读取错误...");
      } catch (InvalidConfigurationException var4) {
         this.loger.info("配置文件 " + this.file.getName() + " 格式错误...");
      }

   }


   public void load(File file) throws IOException, InvalidConfigurationException {
      Validate.notNull(file, "File cannot be null");
      FileInputStream stream = new FileInputStream(file);
      this.load((Reader)(new InputStreamReader(stream, Charsets.UTF_8)));
   }

   public void load(Reader reader) throws IOException, InvalidConfigurationException {
      BufferedReader input = reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
      StringBuilder builder = new StringBuilder();

      String line;
      try {
         while((line = input.readLine()) != null) {
            builder.append(line);
            builder.append('\n');
         }
      } finally {
         input.close();
      }

      this.loadFromString(builder.toString());
   }

   public void reload() {
      this.init(this.file);
   }

   public void save() {
      if (this.file == null) {
         this.loger.info("未定义配置文件路径 保存失败!");
      }

      try {
         this.save(this.file);
      } catch (IOException var2) {
         this.loger.info("配置文件 " + this.file.getName() + " 保存错误...");
         var2.printStackTrace();
      }

   }

   public void save(File file) throws IOException {
      Validate.notNull(file, "File cannot be null");
      Files.createParentDirs(file);
      String data = this.saveToString();

      try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)) {
         writer.write(data);
      }

   }

   public String saveToString() {
      this.yamlOptions.setIndent(this.options().indent());
      this.yamlOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
      this.yamlRepresenter.setDefaultFlowStyle(FlowStyle.BLOCK);
      String header = this.buildHeader();
      String dump = this.yaml.dump(this.getValues(false));
      if (dump.equals("{}\n")) {
         dump = "";
      }

      return header + dump;
   }
}

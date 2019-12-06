<a href="http://120.77.237.175:9080/photos/jdbc/">JDBC</a>

# JDBC概述 #

## 数据的持久化 ##

- 持久化(persistence)：**把数据保存到可掉电式存储设备中以供之后使用**。大多数情况下，特别是企业级应用，**数据持久化意味着将内存中的数据保存到硬盘**上加以”固化”**，而持久化的实现过程大多通过各种关系数据库来完成**。

- 持久化的主要应用是将内存中的数据存储在关系型数据库中，当然也可以存储在磁盘文件、XML数据文件中。

	![](http://120.77.237.175:9080/photos/jdbc/01.png)

## Java中的数据存储技术 ##

- 在Java中，数据库存取技术可分为如下几类：
	- **JDBC**直接访问数据库
	- JDO (Java Data Object )技术
	- **第三方O/R工具**，如Hibernate, Mybatis 等
- JDBC是java访问数据库的基石，JDO、Hibernate、MyBatis等只是更好的封装了JDBC。 

## JDBC介绍 ##

- JDBC(Java Database Connectivity)是一个**独立于特定数据库管理系统、通用的SQL数据库存取和操作的公共接口**（一组API），定义了用来访问数据库的标准Java类库，（**java.sql,javax.sql**）使用这些类库可以以一种**标准**的方法、方便地访问数据库资源。
- JDBC为访问不同的数据库提供了一种**统一的途径**，为开发者屏蔽了一些细节问题。
- JDBC的目标是使Java程序员使用JDBC可以连接任何**提供了JDBC驱动程序**的数据库系统，这样就使得程序员无需对特定的数据库系统的特点有过多的了解，从而大大简化和加快了开发过程。
- 如果没有JDBC，那么Java程序访问数据库时是这样的：

![](http://120.77.237.175:9080/photos/jdbc/02.png)

***
- 有了JDBC，Java程序访问数据库时是这样的：

![](http://120.77.237.175:9080/photos/jdbc/03.png)

***

![](http://120.77.237.175:9080/photos/jdbc/04.png)

## JDBC体系结构 ##

- JDBC接口（API）包括两个层次：
  - **面向应用的API**：Java API，抽象接口，供应用程序开发人员使用（连接数据库，执行SQL语句，获得结果）。
  -  **面向数据库的API**：Java Driver API，供开发商开发数据库驱动程序用。

> **JDBC是sun公司提供一套用于数据库操作的接口，java程序员只需要面向这套接口编程即可。**
>
> **不同的数据库厂商，需要针对这套接口，提供不同实现。不同的实现的集合，即为不同数据库的驱动。																————面向接口编程**

## 1.5 JDBC程序编写步骤 ##

![](http://120.77.237.175:9080/photos/jdbc/05.png)

> 补充：ODBC(**Open Database Connectivity**，开放式数据库连接)，是微软在Windows平台下推出的。使用者在程序中只需要调用ODBC API，由 ODBC 驱动程序将调用转换成为对特定的数据库的调用请求。

# 获取数据库连接 #

## 要素一：Driver接口实现类 ##

### Driver接口介绍 ###

- java.sql.Driver 接口是所有 JDBC 驱动程序需要实现的接口。这个接口是提供给数据库厂商使用的，不同数据库厂商提供不同的实现。

- 在程序中不需要直接去访问实现了 Driver 接口的类，而是由驱动程序管理器类(java.sql.DriverManager)去调用这些Driver实现。
  - Oracle的驱动：**oracle.jdbc.driver.OracleDriver**
  - mySql的驱动： **com.mysql.jdbc.Driver**

	**pom**


		<?xml version="1.0" encoding="UTF-8"?>
		<project xmlns="http://maven.apache.org/POM/4.0.0"
		         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
		    <modelVersion>4.0.0</modelVersion>
		
		    <groupId>com.jdbc</groupId>
		    <artifactId>JDBC</artifactId>
		    <version>1.0-SNAPSHOT</version>
		
		    <dependencies>
		        <dependency>
		            <groupId>mysql</groupId>
		            <artifactId>mysql-connector-java</artifactId>
		            <version>5.1.48</version>
		        </dependency>
		    </dependencies>
		    <build>
		        <plugins>
		            <!-- java编译插件 -->
		            <plugin>
		                <groupId>org.apache.maven.plugins</groupId>
		                <artifactId>maven-compiler-plugin</artifactId>
		                <version>3.2</version>
		                <configuration>
		                    <source>1.8</source>
		                    <target>1.8</target>
		                    <encoding>UTF-8</encoding>
		                </configuration>
		            </plugin>
		        </plugins>
		    </build>
		</project>

### 加载与注册JDBC驱动 ###

- 加载驱动：加载 JDBC 驱动需调用 Class 类的静态方法 forName()，向其传递要加载的 JDBC 驱动的类名
	-  **Class.forName(“com.mysql.jdbc.Driver”);**

- 注册驱动：DriverManager 类是驱动程序管理器类，负责管理驱动程序
	- **使用DriverManager.registerDriver(com.mysql.jdbc.Driver)来注册驱动**

	- 通常不用显式调用 DriverManager 类的 registerDriver() 方法来注册驱动程序类的实例，因为 Driver 接口的驱动程序类**都**包含了静态代码块，在这个静态代码块中，会调用 DriverManager.registerDriver() 方法来注册自身的一个实例。下图是MySQL的Driver实现类的源码：

		![](http://120.77.237.175:9080/photos/jdbc/06.png)

## 要素二：URL ##

- JDBC URL 用于标识一个被注册的驱动程序，驱动程序管理器通过这个 URL 选择正确的驱动程序，从而建立到数据库的连接。

- JDBC URL的标准由三部分组成，各部分间用冒号分隔。 
  - **jdbc:子协议:子名称**
  - **协议**：JDBC URL中的协议总是jdbc 
  - **子协议**：子协议用于标识一个数据库驱动程序
  - **子名称**：一种标识数据库的方法。子名称可以依不同的子协议而变化，用子名称的目的是为了**定位数据库**提供足够的信息。包含**主机名**(对应服务端的ip地址)**，端口号，数据库名**

- 举例：

	![](http://120.77.237.175:9080/photos/jdbc/07.png)

- **几种常用数据库的 JDBC URL**

  - MySQL的连接URL编写方式：

	    - jdbc:mysql://主机名称:mysql服务端口号/数据库名称?参数=值&参数=值
	    - jdbc:mysql://localhost:3306/atguigu
	    - jdbc:mysql://localhost:3306/atguigu**?useUnicode=true&characterEncoding=utf8**（如果JDBC程序与服务器端的字符集不一致，会导致乱码，那么可以通过参数指定服务器端的字符集）
	    - jdbc:mysql://localhost:3306/atguigu?user=root&password=123456

  - Oracle 9i的连接URL编写方式：

	    - jdbc:oracle:thin:@主机名称:oracle服务端口号:数据库名称
	    - jdbc:oracle:thin:@localhost:1521:atguigu

  - SQLServer的连接URL编写方式：

	    - jdbc:sqlserver://主机名称:sqlserver服务端口号:DatabaseName=数据库名称
	    - jdbc:sqlserver://localhost:1433:DatabaseName=atguigu

## 要素三：用户名和密码 ##

- user,password可以用“属性名=属性值”方式告诉数据库
- 可以调用 DriverManager 类的 getConnection() 方法建立到数据库的连接

## 数据库连接方式举例 ##

### 连接方式一 ###

    @Test
    public void testConnection1() throws SQLException
    {
        //1.提供java.sql.Driver接口实现类的对象
        Driver driver = new com.mysql.jdbc.Driver();

        //2.提供url，指明具体操作的数据
        String url = "jdbc:mysql://120.77.237.175:9306/jdbc?serverTimezone=GMT&useSSL=false&characterEncoding=utf-8";
        // jdbc:mysql:协议
        // localhost:ip地址
        // 3306：默认mysql的端口号
        // test:test数据库

        //3.提供Properties的对象，指明用户名和密码
        Properties properties = new Properties();
        properties.setProperty("user","root");
        properties.setProperty("password","123456");

        //4.调用driver的connect()，获取连接
        Connection connect = driver.connect(url, properties);

        System.out.println(connect);
		/**com.mysql.jdbc.JDBC4Connection@5ecddf8f**/
    }

> 说明：上述代码中显式出现了第三方数据库的API

### 连接方式二 ###

    // 方式二：对方式一的迭代:在如下的程序中不出现第三方的api,使得程序具有更好的可移植性
    @Test
    public void testConnection2() throws Exception
    {
        // 1.获取Driver实现类对象：使用反射
        Class<?> clazz = Class.forName("com.mysql.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();

        //2.提供url，指明具体操作的数据
        String url = "jdbc:mysql://120.77.237.175:9306/jdbc?serverTimezone=GMT&useSSL=false&characterEncoding=utf-8";

        //3.提供Properties的对象，指明用户名和密码
        Properties properties = new Properties();
        properties.setProperty("user","root");
        properties.setProperty("password","123456");

        //4.调用driver的connect()，获取连接
        Connection connect = driver.connect(url, properties);

        System.out.println(connect);
    }

> 说明：相较于方式一，这里使用反射实例化Driver，不在代码中体现第三方数据库的API。体现了面向接口编程思想。

### 连接方式三 ###

    //方式三：使用DriverManager替换Driver
    @Test
    public void testConnection3() throws Exception
    {
        // 1.获取Driver实现类对象：使用反射
        Class<?> clazz = Class.forName("com.mysql.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();
        // 注册驱动
        DriverManager.registerDriver(driver);

        //2.提供另外三个连接的基本信息：
        String url = "jdbc:mysql://120.77.237.175:9306/jdbc?serverTimezone=GMT&useSSL=false&characterEncoding=utf-8";
        String user = "root";
        String password = "123456";

        // 获取连接
        Connection connect = DriverManager.getConnection(url, user, password);
        System.out.println(connect);
    }

> 说明：使用DriverManager实现数据库的连接。体会获取连接必要的4个基本要素。

### 连接方式四 ###

    @Test
    public void testConnection4() throws Exception
    {
        //1.提供三个连接的基本信息：
        String url = "jdbc:mysql://120.77.237.175:9306/jdbc?serverTimezone=GMT&useSSL=false&characterEncoding=utf-8";
        String user = "root";
        String password = "123456";

        //2. 获取连接
        Connection connect = DriverManager.getConnection(url, user, password);
        System.out.println(connect);
    }

> 说明：不必显式的注册驱动了。因为在DriverManager的源码中已经存在静态代码块，实现了驱动的注册。

**为什么不需要注册驱动和指定DriverName就可以连接成功**

1. 可看到Driver类在静态类里只要加载了就会调用DriverManager进行注册

		public class Driver extends NonRegisteringDriver implements java.sql.Driver {
		    public Driver() throws SQLException {
		    }
		
		    static {
		        try {
		            DriverManager.registerDriver(new Driver());
		        } catch (SQLException var1) {
		            throw new RuntimeException("Can't register driver!");
		        }
		    }
		}

2. 在包mysql-connector-java-5.1.48.jar!\META-INF\services\java.sql.Driver里可以看到Mysql已经默认配置了驱动类名

		com.mysql.jdbc.Driver
		com.mysql.fabric.jdbc.FabricMySQLDriver

**注意:但不建议把驱动名也省略,如果在其它数据库例 如ORACLE没配置,移植性较差**

	Class.forName("com.mysql.jdbc.Driver");

### 连接方式五 ###

    //方式五(final版)：将数据库连接需要的4个基本信息声明在配置文件中，通过读取配置文件的方式，获取连接
    /*
     * 此种方式的好处？
     * 1.实现了数据与代码的分离。实现了解耦
     * 2.如果需要修改配置文件信息，可以避免程序重新打包。
     */
    @Test
    public void testConnection5() throws Exception {
        //1.读取配置文件中的4个基本信息
        InputStream io = this.getClass().getClassLoader().getResourceAsStream("db.properties");

        System.out.println(io);
        Properties properties = new Properties();
		properties.load(io);

        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");

        //2.加载驱动
        Class.forName(driver);
        //3.获取连接
        Connection connect = DriverManager.getConnection(url, user, password);
        System.out.println(connect);

    }

其中，配置文件声明在工程的src目录下：【jdbc.properties】

	driver=com.mysql.jdbc.Driver
	url=jdbc:mysql://120.77.237.175:9306/jdbc?serverTimezone=GMT&useSSL=false&characterEncoding=utf-8
	user=root
	password=123456

> 说明：使用配置文件的方式保存配置信息，在代码中加载配置文件
>
> **使用配置文件的好处：**
>
> ①实现了代码和数据的分离，如果需要修改配置信息，直接在配置文件中修改，不需要深入代码
> ②如果修改了配置信息，省去重新编译的过程。

# 使用PreparedStatement实现CRUD操作 #

## 操作和访问数据库 ##

- 数据库连接被用于向数据库服务器发送命令和 SQL 语句，并接受数据库服务器返回的结果。其实一个数据库连接就是一个Socket连接。

- 在 java.sql 包中有 3 个接口分别定义了对数据库的调用的不同方式：
  - Statement：用于执行静态 SQL 语句并返回它所生成结果的对象。 
  - PrepatedStatement：SQL 语句被预编译并存储在此对象中，可以使用此对象多次高效地执行该语句。
  - CallableStatement：用于执行 SQL 存储过程

	![](http://120.77.237.175:9080/photos/jdbc/08.png)

## 使用Statement操作数据表的弊端 ##

- 通过调用 Connection 对象的 createStatement() 方法创建该对象。该对象用于执行静态的 SQL 语句，并且返回执行结果。

- Statement 接口中定义了下列方法用于执行 SQL 语句：

	 	int excuteUpdate(String sql)：执行更新操作INSERT、UPDATE、DELETE
  		ResultSet executeQuery(String sql)：执行查询操作SELECT

- 但是使用Statement操作数据表存在弊端：

  - **问题一：存在拼串操作，繁琐**
  - **问题二：存在SQL注入问题**

- SQL 注入是利用某些系统没有对用户输入的数据进行充分的检查，而在用户输入数据中注入非法的 SQL 语句段或命令(如：SELECT user, password FROM user_table WHERE user='a' OR 1 = ' AND password = ' OR '1' = '1') ，从而利用系统的 SQL 引擎完成恶意行为的做法。

- 对于 Java 而言，要防范 SQL 注入，只要用 PreparedStatement(从Statement扩展而来) 取代 Statement 就可以了。

![](http://120.77.237.175:9080/photos/jdbc/13.png)

## PreparedStatement的使用 ##

### PreparedStatement介绍 ###

- 可以通过调用 Connection 对象的 **preparedStatement(String sql)** 方法获取 PreparedStatement 对象

- **PreparedStatement 接口是 Statement 的子接口，它表示一条预编译过的 SQL 语句**

- PreparedStatement 对象所代表的 SQL 语句中的参数用问号(?)来表示，调用 PreparedStatement 对象的 setXxx() 方法来设置这些参数. setXxx() 方法有两个参数，第一个参数是要设置的 SQL 语句中的参数的索引(从 1 开始)，第二个是设置的 SQL 语句中的参数的值

### PreparedStatement vs Statement ###

- 代码的可读性和可维护性。

- **PreparedStatement 能最大可能提高性能：**
  - DBServer会对**预编译**语句提供性能优化。因为预编译语句有可能被重复调用，所以<u>语句在被DBServer的编译器编译后的执行代码被缓存下来，那么下次调用时只要是相同的预编译语句就不需要编译，只要将参数直接传入编译过的语句执行代码中就会得到执行。</u>
  - 在statement语句中,即使是相同操作但因为数据内容不一样,所以整个语句本身不能匹配,没有缓存语句的意义.事实是没有数据库会对普通语句编译后的执行代码缓存。这样<u>每执行一次都要对传入的语句编译一次。</u>
  - (语法检查，语义检查，翻译成二进制命令，缓存)

- PreparedStatement 可以防止 SQL 注入 PreparedStatement还有哪些好处?
	- PreparedStatement操作Blob的数据,而Statement做不到
	- PreparedStatement可以实现更高效的批量操作

除了解决Statement的拼串,SQL问题之外,

### Java与SQL对应数据类型转换表 ###

	| Java类型            | SQL类型                  |
	| ------------------ | ------------------------ |
	| boolean            | BIT                      |
	| byte               | TINYINT                  |
	| short              | SMALLINT                 |
	| int                | INTEGER                  |
	| long               | BIGINT                   |
	| String             | CHAR,VARCHAR,LONGVARCHAR |
	| byte   array       | BINARY  ,    VAR BINARY  |
	| java.sql.Date      | DATE                     |
	| java.sql.Time      | TIME                     |
	| java.sql.Timestamp | TIMESTAMP                |

### 使用PreparedStatement实现增、删、改操作 ###

把建立链接和关闭资源重复的方法建立工具类

	/**
	 * @Description 操作数据库的工具类
	 * @author Lee
	 * @version
	 * @create 2019/12/5 11:50
	 */
	public class JDBCUtils {
	
	    /**
	     *
	     * @Description 获取数据库的连接
	     * @author Lee
	     * @date  2019/12/5 11:50
	     * @return
	     * @throws Exception
	     */
	    public static Connection getConnection() throws IOException,ClassNotFoundException, SQLException {
	        //1.读取配置文件中的4个基本信息
	        InputStream io = JDBCUtils.class.getClassLoader().getResourceAsStream("db.properties");
	
	        Properties properties = new Properties();
	        properties.load(io);
	
	        String driver = properties.getProperty("driver");
	        String url = properties.getProperty("url");
	        String user = properties.getProperty("user");
	        String password = properties.getProperty("password");
	
	        //2.加载驱动
	        Class.forName(driver);
	        //3.获取连接
	        return DriverManager.getConnection(url, user, password);
	    }
	
	
	    public static void closeResource(Connection connection, Statement statement)
	    {
	        try {
	            if (statement !=null)
	            {
	                statement.close();
	            }
	        }catch (SQLException e)
	        {
	            e.printStackTrace();
	        }
	
	        try {
	            if (connection !=null)
	            {
	                connection.close();
	            }
	        }catch (SQLException e)
	        {
	            e.printStackTrace();
	        }
	    }
	
	}

- 添加操作

	    @Test
	    public void testInsert() throws Exception
	    {
	        Connection connection = null;
	        PreparedStatement preparedStatement = null;
	        try {
	            connection = JDBCUtils.getConnection();
	
	            String sql = "Insert Into customers (name,email,birth) VALUES (? , ? ,?)";
	
	            preparedStatement = connection.prepareStatement(sql);
	            preparedStatement.setString(1,"苍老师");
	            preparedStatement.setString(2,"test@gmial.com");
	
	            LocalDateTime localDateTime = LocalDateTime.of(1986, 11, 02, 0, 0);
	            ZoneId zoneId = ZoneId.systemDefault();
	            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
	
	            Date date = Date.from(zonedDateTime.toInstant());
	
	            preparedStatement.setDate(3,new java.sql.Date(date.getTime()));
	            preparedStatement.execute();
	        }catch (Exception e)
	        {
	            e.printStackTrace();
	        }finally {
	           JDBCUtils.closeResource(connection,preparedStatement);
	        }
	    }

- 更新操作

		  @Test
		    public void testUpdate()
		    {
		        Connection connection = null;
		        PreparedStatement preparedStatement = null;
		        try {
		            //1.获取数据库的连接
		            connection = JDBCUtils.getConnection();
		
		            //2.预编译sql语句，返回PreparedStatement的实例
		            String sql = "UPDATE customers SET name =?,email=?,birth=? where id = ?";
		
		            preparedStatement = connection.prepareStatement(sql);
		            //3.填充占位符
		            preparedStatement.setString(1,"三上老师");
		            preparedStatement.setString(2,"ss@gmial.com");
		
		            LocalDateTime localDateTime = LocalDateTime.of(1986, 11, 01, 0, 0);
		            ZoneId zoneId = ZoneId.systemDefault();
		            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
		
		            Date date = Date.from(zonedDateTime.toInstant());
		
		            preparedStatement.setDate(3,new java.sql.Date(date.getTime()));
		            preparedStatement.setInt(4,21);
		            // 4.执行
					/*
					 * ps.execute():
					 * 如果执行的是查询操作，有返回结果，则此方法返回true;
					 * 如果执行的是增、删、改操作，没有返回结果，则此方法返回false.
					 * ps.executeUpdate()是有返回值
					 */
		            preparedStatement.execute();
		        }catch (Exception e)
		        {
		            e.printStackTrace();
		        }finally {
		            //5.资源的关闭
		            JDBCUtils.closeResource(connection,preparedStatement);
		        }
		
		    } 

- 通用增删改操作

	    //通用的增删改操作
	    private void curd(String sql,Object... args)
	    {
	        Connection connection = null;
	        PreparedStatement preparedStatement = null;
	        try {
	            //1.获取数据库的连接
	            connection = JDBCUtils.getConnection();
	
	            preparedStatement = connection.prepareStatement(sql);
	
	            for (int i = 0; i < args.length; i++) {
	                preparedStatement.setObject(i+1,args[i]);
	            }
	            //4.执行
	            preparedStatement.execute();
	        }catch (Exception e)
	        {
	            e.printStackTrace();
	        }finally {
	            //5.资源的关闭
	            JDBCUtils.closeResource(connection,preparedStatement);
	        }
	    }
	
	    @Test
	    public void TestCurd()
	    {
	       /* String sql = "DELETE FROM customers WHERE id =?";
	        curd(sql,21);*/
	
	       String sql = "UPDATE customers SET name =?,email=?,birth=? where id = ?";
	       curd(sql,"三上老师","ss@gmail.com","1986-11-01",20);
	    }



### 使用PreparedStatement实现查询操作 ###

	    public void selectCustomerById()
	    {
	        Connection connection = null;
	        PreparedStatement preparedStatement = null;
	        ResultSet resultSet = null;
	        try {
	            connection = JDBCUtils.getConnection();
	            String sql = "SELECT id,name,email,birth from customers where id = ?";
	
	            preparedStatement = connection.prepareStatement(sql);
	            preparedStatement.setObject(1,20);
	
	            //执行,并返回结果集
	            resultSet = preparedStatement.executeQuery();
	            //处理结果集
	            if (resultSet.next())   //next()判断结果集的下一条是否有数据,如果有数据返回true,否则返回false
	            {
	                Integer id = (Integer)resultSet.getInt("id");
	                String name = resultSet.getString("name");
	                String email = resultSet.getString("email");
	                java.util.Date birth = resultSet.getDate("birth");
	
	                /*ORM编程思想
	                * 一个数据表对应一个JAVA类
	                * 表中的一条记录对应JAVA类的一个对象
	                * 表中的一个字段对应JAVA类的一个属性
	                */
	                Customer customer = new Customer(id, name, email, birth);
	                System.out.println(customer);
	            }
	        } catch (Exception e)
	        {
	            e.printStackTrace();
	        } finally {
				//关闭资源
	            JDBCUtils.closeResource(connection,preparedStatement,resultSet);
	        }
	
	    }

**注意:在实际开发过程中都会把将数据封装为一个对象**

#### 通用Customers查询操作 ####

	    /*针对于customers表的通用的查询操作*/
	    private Customer selectCustomer(String sql , Object... args)
	    {
	        Connection connection = null;
	        PreparedStatement preparedStatement = null;
	        ResultSet resultSet = null;
	        Customer customer = new Customer();
	        try {
	            connection = JDBCUtils.getConnection();
	
	            preparedStatement = connection.prepareStatement(sql);
	
	            for (int i = 0; i < args.length; i++) {
	                preparedStatement.setObject(i+1,args[i]);
	            }
	            //执行,并返回结果集
	            resultSet = preparedStatement.executeQuery();
	            //获取结果集的元数据
	            ResultSetMetaData metaData = resultSet.getMetaData();
	            //通过ResultSetMetaData获取结果集中的列数
	            int columnCount = metaData.getColumnCount();
	
	            //处理结果集
	            if (resultSet.next())   //next()判断结果集的下一条是否有数据,如果有数据返回true,否则返回false
	            {
	                //处理结果集一行数据中的每一个列
	                for (int i = 0; i < columnCount; i++) {
	                    //获取列值
	                    Object object = resultSet.getObject(i+1);
	                    //获取每个列的列名
	                    String label = metaData.getColumnLabel(i+1);
	                    //通过反射，将对象指定名columnName的属性赋值为指定的值columnValue
	                    Field field = customer.getClass().getDeclaredField(label);
	                    /*
	                    当isAccessible()的结果是false时不允许通过反射访问该字段
	                     当该字段时private修饰时isAccessible()得到的值是false，必须要改成true才可以访问
	                     */
	                    field.setAccessible(true);
	                    field.set(customer,object);
	                }
	
	
	            }
	        } catch (Exception e)
	        {
	            e.printStackTrace();
	        } finally {
	            //关闭资源
	            JDBCUtils.closeResource(connection,preparedStatement,resultSet);
	        }
	        return customer;
	    }
	
	    @Test
	    public void testCustomerForQuery()
	    {
	
	      /*  String sql = "SELECT id,name,email,birth from customers where id = ?";
	        Customer customer = this.selectCustomer(sql, 20);
	        System.out.println(customer);*/
	
	        String sql = "SELECT id,name,email,birth from customers where name = ?";
	        Customer customer = this.selectCustomer(sql, "三上老师");
	        System.out.println(customer);
	    }

#### 通用Order查询操作(与customers的区别在于order表字段有下划线) ####

	    /*
	     * 针对于表的字段名与类的属性名不相同的情况：
	     * 1. 必须声明sql时，使用类的属性名来命名字段的别名
	     * 2. 使用ResultSetMetaData时，需要使用getColumnLabel()来替换getColumnName(),
	     *    获取列的别名。
	     *  说明：如果sql中没有给字段其别名，getColumnLabel()获取的就是列名
	     *
	     *
	     */
	
	    /*针对于order表的通用的查询操作*/
	    private Order selectOrder(String sql , Object... args)
	    {
	        Connection connection = null;
	        PreparedStatement preparedStatement = null;
	        ResultSet resultSet = null;
	        Order order = new Order();
	        try {
	            connection = JDBCUtils.getConnection();
	
	            preparedStatement = connection.prepareStatement(sql);
	
	            for (int i = 0; i < args.length; i++) {
	                preparedStatement.setObject(i+1,args[i]);
	            }
	            //执行,并返回结果集
	            resultSet = preparedStatement.executeQuery();
	            //获取结果集的元数据
	            ResultSetMetaData metaData = resultSet.getMetaData();
	            //通过ResultSetMetaData获取结果集中的列数
	            int columnCount = metaData.getColumnCount();
	
	            //处理结果集
	            if (resultSet.next())   //next()判断结果集的下一条是否有数据,如果有数据返回true,否则返回false
	            {
	                //处理结果集一行数据中的每一个列
	                for (int i = 0; i < columnCount; i++) {
	                    //获取列值
	                    Object object = resultSet.getObject(i+1);
	                    //获取每个列的列名
	                    //通过ResultSetMetaData
	                    //获取列的列名：getColumnName() --不推荐使用
	                    //获取列的别名：getColumnLabel()
	                    String label = metaData.getColumnLabel(i+1);
	                    //通过反射，将对象指定名columnName的属性赋值为指定的值columnValue
	                    Field field = order.getClass().getDeclaredField(label);
	                    /*
	                    当isAccessible()的结果是false时不允许通过反射访问该字段
	                     当该字段时private修饰时isAccessible()得到的值是false，必须要改成true才可以访问
	                     */
	                    field.setAccessible(true);
	                    field.set(order,object);
	                }
	
	
	            }
	        } catch (Exception e)
	        {
	            e.printStackTrace();
	        } finally {
	            //关闭资源
	            JDBCUtils.closeResource(connection,preparedStatement,resultSet);
	        }
	        return order;
	    }
	
	    @Test
	    public void testCustomerForQuery()
	    {
	        String sql = "SELECT order_id orderId,order_name orderName,order_date orderDate from `order` where order_id = ?";
	        Order order = this.selectOrder(sql, 1);
	        System.out.println(order);
			/**Order{orderId=1, orderName='AA', orderDate=2010-03-04}**/
	    }

**注意:针对order表的字段是有下划线,因此可以使用别名和getColumnLabel()获取别名指定到反射类的属性里**

#### 通用查询操作(根据指定条件获取单条数据) ####

		/*通过反射把获取Class,根据传过来的泛型返回*/
	    private <T> T getInstanceForSelectByCondition(Class<T> clazz, String sql , Object... args) throws Exception
	    {
	        Connection connection = null;
	        PreparedStatement preparedStatement = null;
	        ResultSet resultSet = null;
	        T t = clazz.newInstance();
	        try {
	            connection = JDBCUtils.getConnection();
	
	            preparedStatement = connection.prepareStatement(sql);
	
	            for (int i = 0; i < args.length; i++) {
	                preparedStatement.setObject(i+1,args[i]);
	            }
	            //执行,并返回结果集
	            resultSet = preparedStatement.executeQuery();
	            //获取结果集的元数据
	            ResultSetMetaData metaData = resultSet.getMetaData();
	            //通过ResultSetMetaData获取结果集中的列数
	            int columnCount = metaData.getColumnCount();
	
	            //处理结果集
	            if (resultSet.next())   //next()判断结果集的下一条是否有数据,如果有数据返回true,否则返回false
	            {
	                //处理结果集一行数据中的每一个列
	                for (int i = 0; i < columnCount; i++) {
	                    //获取列值
	                    Object object = resultSet.getObject(i+1);
	                    //获取每个列的列名
	                    //通过ResultSetMetaData
	                    //获取列的列名：getColumnName() --不推荐使用
	                    //获取列的别名：getColumnLabel()
	                    String label = metaData.getColumnLabel(i+1);
	                    //通过反射，将对象指定名columnName的属性赋值为指定的值columnValue
	                    Field field = clazz.getDeclaredField(label);
	                    /*
	                    当isAccessible()的结果是false时不允许通过反射访问该字段
	                     当该字段时private修饰时isAccessible()得到的值是false，必须要改成true才可以访问
	                     */
	                    field.setAccessible(true);
	                    field.set(t,object);
	                }
	
	
	            }
	        } catch (Exception e)
	        {
	            e.printStackTrace();
	        } finally {
	            //关闭资源
	            JDBCUtils.closeResource(connection,preparedStatement,resultSet);
	        }
	        return t;
	    }
	
	
	    @Test
	    public void getInstanceForSelectByConditionTest() throws Exception
	    {
	        String sql1 = "SELECT order_id orderId,order_name orderName,order_date orderDate from `order` where order_id = ?";
	        Order order = this.getInstanceForSelectByCondition(Order.class,sql1, 1);
	        System.out.println(order);
	
	        String sql2 = "SELECT id,name,email,birth from customers where name = ?";
	        Customer customer = this.getInstanceForSelectByCondition(Customer.class,sql2, "三上老师");
	        System.out.println(customer);
			/**
				Order{orderId=1, orderName='AA', orderDate=2010-03-04}
				Customer{id=20, name='三上老师', email='ss@gmail.com', birth=1986-11-01}
			**/
	    }

#### 通用查询操作(返回List集合获取多条数据) ####

	    /*通过反射把获取Class,根据传过来的泛型返回*/
	    private <T> List<T> getInstanceForSelectToList(Class<T> clazz, String sql , Object... args) throws Exception
	    {
	        Connection connection = null;
	        PreparedStatement preparedStatement = null;
	        ResultSet resultSet = null;
	        ArrayList<T> list = new ArrayList<>();
	        try {
	            connection = JDBCUtils.getConnection();
	
	            preparedStatement = connection.prepareStatement(sql);
	
	            for (int i = 0; i < args.length; i++) {
	                preparedStatement.setObject(i+1,args[i]);
	            }
	            //执行,并返回结果集
	            resultSet = preparedStatement.executeQuery();
	            //获取结果集的元数据
	            ResultSetMetaData metaData = resultSet.getMetaData();
	            //通过ResultSetMetaData获取结果集中的列数
	            int columnCount = metaData.getColumnCount();
	
	            //处理结果集
	            while (resultSet.next())   //next()判断结果集的下一条是否有数据,如果有数据返回true,否则返回false
	            {
					 T t = clazz.newInstance();
	                //处理结果集一行数据中的每一个列
	                for (int i = 0; i < columnCount; i++) {
	                    //获取列值
	                    Object object = resultSet.getObject(i+1);
	                    //获取每个列的列名
	                    //通过ResultSetMetaData
	                    //获取列的列名：getColumnName() --不推荐使用
	                    //获取列的别名：getColumnLabel()
	                    String label = metaData.getColumnLabel(i+1);
	                    //通过反射，将对象指定名columnName的属性赋值为指定的值columnValue
	                    Field field = clazz.getDeclaredField(label);
	                    /*
	                    当isAccessible()的结果是false时不允许通过反射访问该字段
	                     当该字段时private修饰时isAccessible()得到的值是false，必须要改成true才可以访问
	                     */
	                    field.setAccessible(true);
	                    field.set(t,object);
	                }
	
	                list.add(t);
	            }
	        } catch (Exception e)
	        {
	            e.printStackTrace();
	        } finally {
	            //关闭资源
	            JDBCUtils.closeResource(connection,preparedStatement,resultSet);
	        }
	        return list;
	    }
	
	    @Test
	    public void getInstanceForSelectToListTest() throws Exception{
	        String sql1 = "SELECT order_id orderId,order_name orderName,order_date orderDate from `order`";
	        List<Order> orders = this.getInstanceForSelectToList(Order.class, sql1);
	        orders.forEach(System.out::println);
	
	        String sql2 = "SELECT id,name,email,birth from customers";
	        List<Customer> customers = this.getInstanceForSelectToList(Customer.class,sql2);
	       customers.forEach(System.out::println);

			/**
				Order{orderId=1, orderName='AA', orderDate=2010-03-04}
				Order{orderId=2, orderName='BB', orderDate=2000-02-01}
				Order{orderId=4, orderName='GG', orderDate=1994-06-28}
				Customer{id=1, name='汪峰', email='wf@126.com', birth=2010-02-02}
				Customer{id=2, name='王菲', email='wangf@163.com', birth=1988-12-26}
				Customer{id=3, name='林志玲', email='linzl@gmail.com', birth=1984-06-12}
				Customer{id=4, name='汤唯', email='tangw@sina.com', birth=1986-06-13}
			**/
	    }

> 说明：使用PreparedStatement实现的查询操作可以替换Statement实现的查询操作，解决Statement拼串和SQL注入问题。

## ResultSet与ResultSetMetaData ##

### ResultSet ###

- 查询需要调用PreparedStatement 的 executeQuery() 方法，查询结果是一个ResultSet 对象

- ResultSet 对象以逻辑表格的形式封装了执行数据库操作的结果集，ResultSet 接口由数据库厂商提供实现
- ResultSet 返回的实际上就是一张数据表。有一个指针指向数据表的第一条记录的前面。

- ResultSet 对象维护了一个指向当前数据行的**游标**，初始的时候，游标在第一行之前，可以通过 ResultSet 对象的 next() 方法移动到下一行。调用 next()方法检测下一行是否有效。若有效，该方法返回 true，且指针下移。相当于Iterator对象的 hasNext() 和 next() 方法的结合体。
- 当指针指向一行时, 可以通过调用 getXxx(int index) 或 getXxx(int columnName) 获取每一列的值。

  - 例如: getInt(1), getString("name")
  - **注意：Java与数据库交互涉及到的相关Java API中的索引都从1开始。**

- ResultSet 接口的常用方法：
  - boolean next()

  - getString()
  - …

	![](http://120.77.237.175:9080/photos/jdbc/09.png)

### ResultSetMetaData ###

- 可用于获取关于 ResultSet 对象中列的类型和属性信息的对象

- ResultSetMetaData meta = rs.getMetaData();
  - **getColumnName**(int column)：获取指定列的名称
  - **getColumnLabel**(int column)：获取指定列的别名
  - **getColumnCount**()：返回当前 ResultSet 对象中的列数。 

  - getColumnTypeName(int column)：检索指定列的数据库特定的类型名称。 
  - getColumnDisplaySize(int column)：指示指定列的最大标准宽度，以字符为单位。 
  - **isNullable**(int column)：指示指定列中的值是否可以为 null。 

  -  isAutoIncrement(int column)：指示是否自动为指定列进行编号，这样这些列仍然是只读的。 

		![](http://120.77.237.175:9080/photos/jdbc/10.png)

**问题1：得到结果集后, 如何知道该结果集中有哪些列 ？ 列名是什么？**

​     需要使用一个描述 ResultSet 的对象， 即 ResultSetMetaData

**问题2：关于ResultSetMetaData**

1. **如何获取 ResultSetMetaData**： 调用 ResultSet 的 getMetaData() 方法即可
2. **获取 ResultSet 中有多少列**：调用 ResultSetMetaData 的 getColumnCount() 方法
3. **获取 ResultSet 每一列的列的别名是什么**：调用 ResultSetMetaData 的getColumnLabel() 方法

![](http://120.77.237.175:9080/photos/jdbc/11.png)

## 资源的释放 ##

- 释放ResultSet, Statement,Connection。
- 数据库连接（Connection）是非常稀有的资源，用完后必须马上释放，如果Connection不能及时正确的关闭将导致系统宕机。Connection的使用原则是**尽量晚创建，尽量早的释放。**
- 可以在finally中关闭，保证及时其他代码出现异常，资源也一定能被关闭。

## JDBC API小结 ##

- 两种思想
  - 面向接口编程的思想

  - ORM思想(object relational mapping)
    - 一个数据表对应一个java类
    - 表中的一条记录对应java类的一个对象
    - 表中的一个字段对应java类的一个属性

  > sql是需要结合列名和表的属性名来写。注意起别名。

- 两种技术
  - JDBC结果集的元数据：ResultSetMetaData
    - 获取列数：getColumnCount()
    - 获取列的别名：getColumnLabel()
  - 通过反射，创建指定类的对象，获取指定的属性并赋值

***

# 操作BLOB类型字段 #

## MySQL BLOB类型 ##

- MySQL中，BLOB是一个二进制大型对象，是一个可以存储大量数据的容器，它能容纳不同大小的数据。
- 插入BLOB类型的数据必须使用PreparedStatement，因为BLOB类型的数据无法使用字符串拼接写的。
- MySQL的四种BLOB类型(除了在存储的最大信息量上不同外，他们是等同的)

![](http://120.77.237.175:9080/photos/jdbc/12.png)

- 实际使用中根据需要存入的数据大小定义不同的BLOB类型。
- 需要注意的是：如果存储的文件过大，数据库的性能会下降。
- 如果在指定了相关的Blob类型以后，还报错：xxx too large，那么在mysql的安装目录下，找my.ini文件加上如下的配置参数： **max_allowed_packet=16M**。同时注意：修改了my.ini文件之后，需要重新启动mysql服务。

## 向数据表中插入大数据类型 ##

	//获取连接
	Connection conn = JDBCUtils.getConnection();
			
	String sql = "insert into customers(name,email,birth,photo)values(?,?,?,?)";
	PreparedStatement ps = conn.prepareStatement(sql);
	
	// 填充占位符
	ps.setString(1, "徐海强");
	ps.setString(2, "xhq@126.com");
	ps.setDate(3, new Date(new java.util.Date().getTime()));
	// 操作Blob类型的变量
	FileInputStream fis = new FileInputStream("xhq.png");
	ps.setBlob(4, fis);
	//执行
	ps.execute();
			
	fis.close();
	JDBCUtils.closeResource(conn, ps);

## 修改数据表中的Blob类型字段 ##

	Connection conn = JDBCUtils.getConnection();
	String sql = "update customers set photo = ? where id = ?";
	PreparedStatement ps = conn.prepareStatement(sql);
	
	// 填充占位符
	// 操作Blob类型的变量
	FileInputStream fis = new FileInputStream("coffee.png");
	ps.setBlob(1, fis);
	ps.setInt(2, 25);
	
	ps.execute();

## 从数据表中读取大数据类型 ##

	String sql = "SELECT id, name, email, birth, photo FROM customer WHERE id = ?";
	conn = getConnection();
	ps = conn.prepareStatement(sql);
	ps.setInt(1, 8);
	rs = ps.executeQuery();
	if(rs.next()){
		Integer id = rs.getInt(1);
	    String name = rs.getString(2);
		String email = rs.getString(3);
	    Date birth = rs.getDate(4);
		Customer cust = new Customer(id, name, email, birth);
	    System.out.println(cust); 
	    //读取Blob类型的字段
		Blob photo = rs.getBlob(5);
		InputStream is = photo.getBinaryStream();
		OutputStream os = new FileOutputStream("c.jpg");
		byte [] buffer = new byte[1024];
		int len = 0;
		while((len = is.read(buffer)) != -1){
			os.write(buffer, 0, len);
		}
	    JDBCUtils.closeResource(conn, ps, rs);
			
		if(is != null){
			is.close();
		}
			
		if(os !=  null){
			os.close();
		}
	    
	}

# 批量插入 #

## 批量执行SQL语句 ##

当需要成批插入或者更新记录时，可以采用Java的批量**更新**机制，这一机制允许多条语句一次性提交给数据库批量处理。通常情况下比单独提交处理更有效率

JDBC的批量处理语句包括下面三个方法：

- **addBatch(String)：添加需要批量处理的SQL语句或是参数；**
- **executeBatch()：执行批量处理语句；**
- **clearBatch():清空缓存的数据**

通常我们会遇到两种批量执行SQL语句的情况：

- 多条SQL语句的批量处理；
- 一个SQL语句的批量传参；

## 高效的批量插入 ##

# 数据库事务 #

## 数据库事务介绍 ##

- **事务：一组逻辑操作单元,使数据从一种状态变换到另一种状态。**

- **事务处理（事务操作）：**保证所有事务都作为一个工作单元来执行，即使出现了故障，都不能改变这种执行方式。当在一个事务中执行多个操作时，要么所有的事务都**被提交(commit)**，那么这些修改就永久地保存下来；要么数据库管理系统将放弃所作的所有修改，整个事务**回滚(rollback)**到最初状态。

- 为确保数据库中数据的**一致性**，数据的操纵应当是离散的成组的逻辑单元：当它全部完成时，数据的一致性可以保持，而当这个单元中的一部分操作失败，整个事务应全部视为错误，所有从起始点以后的操作应全部回退到开始状态。

## JDBC事务处理 ##

- 数据一旦提交，就不可回滚。
- 数据什么时候意味着提交？
  - **当一个连接对象被创建时，默认情况下是自动提交事务**：每次执行一个 SQL 语句时，如果执行成功，就会向数据库自动提交，而不能回滚。
  - **关闭数据库连接，数据就会自动的提交。**如果多个操作，每个操作使用的是自己单独的连接，则无法保证事务。即同一个事务的多个操作必须在同一个连接下。
- **JDBC程序中为了让多个 SQL 语句作为一个事务执行：**

  - 调用 Connection 对象的 **setAutoCommit(false);** 以取消自动提交事务
  - 在所有的 SQL 语句都成功执行后，调用 **commit();** 方法提交事务
  - 在出现异常时，调用 **rollback();** 方法回滚事务

  > 若此时 Connection 没有被关闭，还可能被重复使用，则需要恢复其自动提交状态 setAutoCommit(true)。尤其是在使用数据库连接池技术时，执行close()方法前，建议恢复自动提交状态。

## 事务的ACID属性 ##

1. **原子性（Atomicity）**
    原子性是指事务是一个不可分割的工作单位，事务中的操作要么都发生，要么都不发生。 

2. **一致性（Consistency）**
    事务必须使数据库从一个一致性状态变换到另外一个一致性状态。

3. **隔离性（Isolation）**
    事务的隔离性是指一个事务的执行不能被其他事务干扰，即一个事务内部的操作及使用的数据对并发的其他事务是隔离的，并发执行的各个事务之间不能互相干扰。

4. **持久性（Durability）**
    持久性是指一个事务一旦被提交，它对数据库中数据的改变就是永久性的，接下来的其他操作和数据库故障不应该对其有任何影响。

### 数据库的并发问题 ###

- 对于同时运行的多个事务, 当这些事务访问数据库中相同的数据时, 如果没有采取必要的隔离机制, 就会导致各种并发问题:
  - **脏读**: 对于两个事务 T1, T2, T1 读取了已经被 T2 更新但还**没有被提交**的字段。之后, 若 T2 回滚, T1读取的内容就是临时且无效的。
  - **不可重复读**: 对于两个事务T1, T2, T1 读取了一个字段, 然后 T2 **更新**了该字段。之后, T1再次读取同一个字段, 值就不同了。
  - **幻读**: 对于两个事务T1, T2, T1 从一个表中读取了一个字段, 然后 T2 在该表中**插入**了一些新的行。之后, 如果 T1 再次读取同一个表, 就会多出几行。

- **数据库事务的隔离性**: 数据库系统必须具有隔离并发运行各个事务的能力, 使它们不会相互影响, 避免各种并发问题。

- 一个事务与其他事务隔离的程度称为隔离级别。数据库规定了多种事务隔离级别, 不同隔离级别对应不同的干扰程度, **隔离级别越高, 数据一致性就越好, 但并发性越弱。**

### 四种隔离级别 ###

- 数据库提供的4种事务隔离级别：

	![](http://120.77.237.175:9080/photos/jdbc/14.png)
- Oracle 支持的 2 种事务隔离级别：**READ COMMITED**, SERIALIZABLE。 Oracle 默认的事务隔离级别为: **READ COMMITED** 。
- Mysql 支持 4 种事务隔离级别。Mysql 默认的事务隔离级别为: **REPEATABLE READ。**

### 在MySql中设置隔离级别 ###

- 每启动一个 mysql 程序, 就会获得一个单独的数据库连接. 每个数据库连接都有一个全局变量 @@tx_isolation, 表示当前的事务隔离级别。

- 查看当前的隔离级别: 

 		 SELECT @@tx_isolation;

- 设置当前 mySQL 连接的隔离级别:  

  		set  transaction isolation level read committed;


- 设置数据库系统的全局的隔离级别:

  		set global transaction isolation level read committed;

- 补充操作：

  - 创建mysql数据库用户：

   			 create user tom identified by 'abc123';


  - 授予权限

		    #授予通过网络方式登录的tom用户，对所有库所有表的全部权限，密码设为abc123.
		    grant all privileges on *.* to tom@'%'  identified by 'abc123'; 
		    
		     #给tom用户使用本地命令行方式，授予atguigudb这个库下的所有表的插删改查的权限。
		    grant select,insert,delete,update on atguigudb.* to tom@localhost identified by 'abc123'; 
    
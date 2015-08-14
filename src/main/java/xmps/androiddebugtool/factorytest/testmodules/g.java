package xmps.androiddebugtool.factorytest.testmodules;

public class g {
	public final static String tag = "FactoryTestTool";
	/**全局的流程配置，应该由MainAcFmManager初始化，其他的模块可以读取，但是不应该改动它*/
	public static TestProcedures tp = null;



	/*-----------------------------------------------
	 * msg filter
	 * -----------------------------------------------*/
	public final static String MSG_FILTER_KEYPAD_LAYOUT_TEST1_KEY1 = "KaypadLayoutTest1Key1";
	public final static String MSG_FILTER_KEYPAD_LAYOUT_TEST1_KEY2 = "KaypadLayoutTest1Key2";
	public final static String MSG_FILTER_KEYPAD_LAYOUT_TEST1_KEY3 = "KaypadLayoutTest1Key3";
}

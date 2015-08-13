package com.android.enjack.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;


/**
说明：文件操作封装

history：-------------------------->

20130528：初次版本
20130529: 添加getSuffix、setSuffix

 */
public class FileIO {

	final private boolean bDebug = true;
	private int FileCount;
	private ArrayList FileList ;
	
	public FileIO()
	{
		FileCount = 0;
		FileList = new ArrayList();
	}


	/**
	 * 说明：判断SD是否可读写
	 */
	public boolean isSDAccess()
	{
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state))
			return true;
		else
			return false;
	}

	/**
	 说明:得到SD卡的目录名称
	 */
	public String getSDDirectoryName()
	{
		File file = Environment.getExternalStorageDirectory();
		return file.getAbsolutePath();
	}


	/**
	 * 获取文件的后缀名，123.txt则返回.txt
	 * */
	public String getSuffix(File file)
	{
		String suffix = null;
		String filename = file.getName();
		
		if(file.isFile())
		{
			if(filename != null  && filename.length()>0)
			{
				int dot = filename.lastIndexOf('.');
				if(dot > -1  && dot < filename.length())
				{
					suffix = filename.substring(dot);
				}
			}
		}
		
		return suffix;
	}


	/**
	 * 修改文件后缀名.</br>
	 * 注意：</br>
	 * 1.没有后缀名的将添加上后缀名，有后缀名的修改成新的后缀</br>
	 *  2.newSuffix可以为空，newSuffix=""，则将原本的后缀去掉</br>
	 *   3.该函数不会检查输入的新的后缀名是否合法
	 *
	 * @param file
	 * 	file
	 * @param newSuffix
	 * 	新的后缀名
	 *
	 * @return
	 * 	新的后缀名
	 * */
	public boolean setSuffix(File file, String newSuffix)
	{
		boolean bRet = false;
		
		if(file.isFile())
		{
			String suffix = getSuffix(file);
			if(null != suffix)//�к�׺��
			{
				int len = suffix.length();
				if(len <= file.getName().length())
				{
					String filename = file.getName();
					String newFilename = null;
					
					try
					{
						newFilename = filename.substring(0, filename.length()-len) + newSuffix;
					}
					catch(IndexOutOfBoundsException e)
					{
						fileLog("setSuffix catch exception.String.substring out of bound");
						bRet = false;
					}
					finally
					{
						bRet = reName(file.getAbsolutePath(), newFilename);
					}
				}
					
			}
			else//û�к�׺��,ֱ�Ӽ��Ϻ�׺
			{
				bRet = reName(file.getAbsolutePath(),
								file.getName()+newSuffix);
			}
		}
		
		return bRet;
	}


	/**
	 * 得到目录下所有文件的总数，包括子目录下的文件,但是不包括子目录
	 *
	 * @param	path
	 * 	绝对目录路径
	 * */
	public int getTotalFileCount(String path)
	{
		int cnt = 0;
		
		FileCount = 0;
		cnt = _getTotalFileCount(path);
		FileCount = 0;
		return cnt;
	}


	/**
	 * 得到目录下所有文件的总数，包括子目录下的文件,但不包括子目录本身
	 *
	 * @param path
	 * 	绝对目录路径
	 * */
	private int _getTotalFileCount(String path)
	{
		File file = new File(path);
		File f[];
		
		if(!file.exists())
			return 0;
		
		if(file.isDirectory())
		{
			f = file.listFiles();
			for(int i=0; i<f.length; i++)
			{
				//fileLog(path+File.separator+f[i].getName());
				_getTotalFileCount(path+File.separator+f[i].getName());
			}
		}
		else
		{
			FileCount++;//FileCount ������ȫ�ֵ�������������ڵݹ��ʱ���޷�ʵ���ۼӵ�Ч�� 
		}

		return FileCount;
	}


	/**
	 * 获取某个目录下的所有文件(不包括子目录)
	 *
	 * @param	path
	 * 	绝对路径
	 *
	 * @return
	 * 	所有文件的集合String[]  （绝对路径）
	 * */
	public String[] getAllFileName(String path)
	{
		String fileName[];
		
		FileList.clear();
		_getAllFileName(path);
		fileName = new String[FileList.size()];
		for(int i=0; i<FileList.size(); i++)
		{
			fileName[i]=(String)FileList.get(i);
			//fileLog(fileName[i]);
		}
		FileList.clear();
		
		return fileName;
	}


	/**
	 * 获取某个目录下的所有文件(不包括子目录)
	 *
	 * @param path
	 * 	绝对路径
	 *
	 * @return
	 * 	所有文件的集合String[]  （绝对路径）
	 * */
	private void _getAllFileName(String path)
	{
		
		File file = new File(path);
		File f[];

		
		if(!file.exists())
			return ;
		
		if(file.isDirectory())
		{
			f = file.listFiles();
			for(int i=0; i<f.length; i++)
			{
				//fileLog(path+File.separator+f[i].getName());
				_getAllFileName(path+File.separator+f[i].getName());
			}
		}
		else
		{

			FileList.add(path);
		
		}


	}


	/**
	 * 获取某个目录下所有的子目录
	 *
	 * @param	path
	 * 		绝对路径
	 *
	 * @return
	 * 	所有子目录  （绝对路径）
	 * */
	public String[] getAllDirectoryName(String path)
	{
		String fileName[];
		
		FileList.clear();
		_getAllDirectoryName(path);
		fileName = new String[FileList.size()];
		for(int i=0; i<FileList.size(); i++)
		{
			fileName[i]=(String)FileList.get(i);
			//fileLog(fileName[i]);
		}
		FileList.clear();
		
		return fileName;		
	}


	/**
	 * 获取某个目录下所有的子目录
	 *
	 * @param path
	 * 	绝对路径
	 * */
	private void _getAllDirectoryName(String path)
	{
		
		File file = new File(path);
		File f[];

		
		if(!file.exists())
			return ;
		
		if(file.isDirectory())
		{
			f = file.listFiles();
			for(int i=0; i<f.length; i++)
			{
				//fileLog(path+File.separator+f[i].getName());
				if(f[i].isDirectory())
				{
					FileList.add(path+File.separator+f[i].getName());
					_getAllDirectoryName(path+File.separator+f[i].getName());
				}
				
			}
		}
	}

	/**
	 * 说明：删除文件
	 * @param path
	 * 	文件路径
	 * @return
	 * 	true表示成功
	 */
	public boolean delFile(String path)
	{
		File file = new File(path);
		boolean bRet = false;
		
		if(!file.exists())
		{
			bRet = true;
			fileLog("the file to del is no exist");
		}
		
		
		if(!file.delete())
		{
			fileLog("del file failed");
			bRet = true;
		}
		
		return bRet;
	}



	/**
	 * 说明：删除指定目录下所有文件,也包括改目录
	 * @param path
	 * 	指定的目录
	 */
	public void delAllFiles(String path)
	{
		
		File file = new File(path);
		File f[];
		
		if(!file.exists())
			return ;
		
		if(file.isDirectory())
		{
			f = file.listFiles();
			for(int i=0; i<f.length; i++)
			{
				delAllFiles(path+File.separator+f[i].getName());
			}
			
			file.delete();//ɾ����ǰĿ¼
		}
		else
		{
			file.delete();		
		}
		
		
	}


	/**
	 * 说明：重命名文件
	 * @param path
	 * 	输入的文件或目录路径
	 * @param newName
	 *	新的名字
	 */
	public boolean reName(String path, String newName)
	{
		File file = new File(path);
		
		if(!file.exists())
			return false;
		boolean bRet = file.renameTo(new File(file.getParent()+ File.separator+ newName));
		if(!bRet)
			fileLog("rename file failed");
		return bRet;
	}
	
	
	private void fileLog(String str)
	{
		if(!bDebug)
			return;
		Log.i("FILE_LOG",str);
	}
	
}


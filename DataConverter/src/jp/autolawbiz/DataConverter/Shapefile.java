package jp.autolawbiz.DataConverter;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Shapefile extends Conversion {

	public static void ToGeoJSON(String inputPath, String outputPath) {
		String shapeFile = inputPath;
		String geoJSONFile = outputPath;
		String geoJSONFileCharacterCode = "UTF-8";
	//	String logFileCharacterCode = "UTF-8";
		String dbfFile = shapeFile.replace(".shp", ".dbf");

		BufferedInputStream bi = null;
		DataInputStream dis = null;
		PrintWriter p_writer = null;
	//	PrintWriter logWriter = null;
		int fileNowLength = 0;
		int fileRecordCount = 0;
		int recordNowCount = 0;

		try {
			//DbfFile読み込み処理へ
			Dbffile[] dbfFields = readDbf(dbfFile);

			//ShapeFile読み込みインスタンス生成
			bi = new BufferedInputStream(new FileInputStream(shapeFile));
			dis = new DataInputStream(bi);

			//GeoJSONFile書き込みインスタンス生成
			p_writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(geoJSONFile),geoJSONFileCharacterCode)));

	//		//logFile書き込みインスタンス生成
	//		logWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile),logFileCharacterCode)));

			//---メイン・ファイル・ヘッダ---
			int fileCode = dis.readInt();
			skipReadInt(5, dis);
			//ファイル長の値はワード単位（16 ビットを１ワードとする）で、ヘッダの 50 ワードを含む全ファイルの長さです。
			int fileLength = dis.readInt();
			int fileVersion = byte2Int(reverse4ByteInt(int2Byte(dis.readInt())));
			int fileShapeType = byte2Int(reverse4ByteInt(int2Byte(dis.readInt())));
			double fileBoundingBoxXmin = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
			double fileBoundingBoxYmin = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
			double fileBoundingBoxXmax = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
			double fileBoundingBoxYmax = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
			double fileBoundingBoxZmin = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
			double fileBoundingBoxZmax = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
			double fileBoundingBoxMmin = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
			double fileBoundingBoxMmax = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
			fileNowLength = 50;

			/*
			//表示
			System.out.println("---メイン・ファイル・ヘッダ---");
			System.out.println("ファイル・コード：" + fileCode);
			System.out.println("ファイル長：" + fileLength);
			System.out.println("バージョン：" + fileVersion);
			System.out.println("シェープ・タイプ：" + fileShapeType);
			System.out.println("バウンディング・ボックスXmin：" + fileBoundingBoxXmin);
			System.out.println("バウンディング・ボックスYmin：" + fileBoundingBoxYmin);
			System.out.println("バウンディング・ボックスXmax：" + fileBoundingBoxXmax);
			System.out.println("バウンディング・ボックスYmax：" + fileBoundingBoxYmax);
			System.out.println("バウンディング・ボックスZmin：" + fileBoundingBoxZmin);
			System.out.println("バウンディング・ボックスZmax：" + fileBoundingBoxZmax);
			System.out.println("バウンディング・ボックスMmin：" + fileBoundingBoxMmin);
			System.out.println("バウンディング・ボックスMmax：" + fileBoundingBoxMmax);
			*/

			//表示
			System.out.println("---Main File Header---");
			System.out.println("File Code " + fileCode);
			System.out.println("File Length " + fileLength);
			System.out.println("Version " + fileVersion);
			System.out.println("Shape Type " + fileShapeType);
			System.out.println("Bounding Box Xmin " + fileBoundingBoxXmin);
			System.out.println("Bounding Box Ymin " + fileBoundingBoxYmin);
			System.out.println("Bounding Box Xmax " + fileBoundingBoxXmax);
			System.out.println("Bounding Box Ymax " + fileBoundingBoxYmax);
			System.out.println("Bounding Box Zmin " + fileBoundingBoxZmin);
			System.out.println("Bounding Box Zmax " + fileBoundingBoxZmax);
			System.out.println("Bounding Box Mmin " + fileBoundingBoxMmin);
			System.out.println("Bounding Box Mmax " + fileBoundingBoxMmax);


			//GeoJSONヘッダ
	        p_writer.println("{");
	        p_writer.println("\t\"type\": \"FeatureCollection\",");
	        p_writer.println("\t\"features\": [");

			while (fileNowLength < fileLength) {
				//---レコード・ヘッダ---
				int recordNumber = dis.readInt();
				fileRecordCount = recordNumber;
				int contentsLength = dis.readInt();
				int shapeType = byte2Int(reverse4ByteInt(int2Byte(dis.readInt())));
				int numParts = 0;
				int numPoints = 0;
				if (fileShapeType == 1) {
					fileNowLength += 6;

					/*
					//表示
					System.out.println("---レコード・ヘッダ---");
					System.out.println("レコード番号：" + recordNumber);
					System.out.println("コンテンツ長：" + contentsLength);
					System.out.println("---メイン・ファイルのレコード・コンテンツ/Point---");
					System.out.println("シェープ・タイプ：" + shapeType);
					*/

					//表示
					System.out.println("---Main File Record Headers---");
					System.out.println("Record Number " + recordNumber);
					System.out.println("Content Length " + contentsLength);
					System.out.println("---Record Contents---");
					System.out.println("Shape Type " + shapeType);

				}
				else if (fileShapeType == 3 || fileShapeType == 5) {
					double boundingBoxXmin = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
					double boundingBoxYmin = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
					double boundingBoxXmax = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
					double boundingBoxYmax = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
					numParts = byte2Int(reverse4ByteInt(int2Byte(dis.readInt())));
					numPoints = byte2Int(reverse4ByteInt(int2Byte(dis.readInt())));
					fileNowLength += 26;

					/*
					//表示
					System.out.println("---レコード・ヘッダ---");
					System.out.println("レコード番号：" + recordNumber);
					System.out.println("コンテンツ長：" + contentsLength);
					System.out.println("---メイン・ファイルのレコード・コンテンツ/Polyline/Polygon---");
					System.out.println("シェープ・タイプ：" + shapeType);
					System.out.println("バウンディング・ボックスXmin：" + boundingBoxXmin);
					System.out.println("バウンディング・ボックスYmin：" + boundingBoxYmin);
					System.out.println("バウンディング・ボックスXmax：" + boundingBoxXmax);
					System.out.println("バウンディング・ボックスYmax：" + boundingBoxYmax);
					System.out.println("NumParts：" + numParts);
					System.out.println("NumPoints：" + numPoints);
					*/

					//表示
					System.out.println("---Main File Record Headers---");
					System.out.println("Record Number " + recordNumber);
					System.out.println("Content Length " + contentsLength);
					System.out.println("---Record Contents---");
					System.out.println("Shape Type " + shapeType);
					System.out.println("Bounding Box Xmin " + boundingBoxXmin);
					System.out.println("Bounding Box Ymin " + boundingBoxYmin);
					System.out.println("Bounding Box Xmax " + boundingBoxXmax);
					System.out.println("Bounding Box Ymax " + boundingBoxYmax);
					System.out.println("NumParts " + numParts);
					System.out.println("NumPoints " + numPoints);

				}
				/*
				 * System.out.println("---メイン・ファイルのレコード・コンテンツ/Point---");
				 */

				//GeoJSONポリゴンヘッダ
	            p_writer.println("\t\t{");
	            p_writer.println("\t\t\t\"type\": \"Feature\",");

	            //GeoJSON属性ヘッダ
	            p_writer.println("\t\t\t\"properties\": {");

	            //GeoJSON属性
	            for (int m = 0; m < dbfFields.length; m++) {
	//            	dbfFields[m].getFieldName();
	//            	dbfFields[m].getListValues().get(0);
	            	if (m == dbfFields.length - 1)
	            		p_writer.println("\t\t\t\t\"" + dbfFields[m].getFieldName() + "\": \""
	                        	+ dbfFields[m].getListValues().get(recordNowCount) + "\"");
	            	else
	            		p_writer.println("\t\t\t\t\"" + dbfFields[m].getFieldName() + "\": \""
	                        	+ dbfFields[m].getListValues().get(recordNowCount) + "\",");
	//            	p_writer.println("\t\t\t\t\"" + dbfFields[m].getFieldName() + "\": \""
	//            	+ dbfFields[m].getListValues().get(recordNowCount) + "\",");
	            }
	//            p_writer.println("\t\t\t\t\"市区町村名\": \"てきとう\",");
	//            p_writer.println("\t\t\t\t\"市区町村名2\": \"てきとう\"");

	            //GeoJSON属性フッタ
	            p_writer.println("\t\t\t},");

	            //GeoJSON図形ヘッダ
	            p_writer.println("\t\t\t\"geometry\": {");
	            if (fileShapeType == 1) {
	            	p_writer.println("\t\t\t\t\"type\": \"Point\",");
	            	p_writer.print("\t\t\t\t\"coordinates\": ");
	        	}
	            else if (fileShapeType == 3) {
	            	p_writer.println("\t\t\t\t\"type\": \"LineString\",");
	            	p_writer.println("\t\t\t\t\"coordinates\": [");
	            }
	            else if (fileShapeType == 5) {
	            	p_writer.println("\t\t\t\t\"type\": \"Polygon\",");
	                p_writer.println("\t\t\t\t\"coordinates\": [");
	                p_writer.println("\t\t\t\t\t[");
	            }

	            if (fileShapeType == 1) {
	            	double dblX = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
					double dblY = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
					fileNowLength += 8;
					/*
					System.out.println("X：" + dblX);
					System.out.println("Y：" + dblY);
					*/

					System.out.println("X " + dblX);
					System.out.println("Y " + dblY);

					String sLine = "[" + dblX
	                        + ", " + dblY
	                        + "]";
					p_writer.println(sLine);
	            }
	            else if (fileShapeType == 3 || fileShapeType == 5) {
					ArrayList<Integer> vStartPos = new ArrayList<Integer>();
					for (int i = 0; i < numParts; i++) {
						int intParts = byte2Int(reverse4ByteInt(int2Byte(dis.readInt())));
						fileNowLength += 2;
						vStartPos.add(intParts);
						/*
						System.out.println("Parts[" + i + "]：" + intParts);
						*/

						System.out.println("Parts[" + i + "] " + intParts);

					}
					if (numParts == 1) {
						String sLine = "";
						for (int j = 0; j < numPoints; j++) {
							double dblX = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
							double dblY = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
							fileNowLength += 8;
							/*
							System.out.println("X[" + j + "]：" + dblX);
							System.out.println("Y[" + j + "]：" + dblY);
							*/

							System.out.println("X[" + j + "] " + dblX);
							System.out.println("Y[" + j + "] " + dblY);


							//最終頂点
	                        if (j == numPoints - 1)
	                        {
	                            sLine += "[" + dblX
	                              + ", " + dblY
	                              + "]";
	                        }
	                        //最終頂点以外の頂点
	                        else
	                        {
	                            sLine += "[" + dblX
	                              + ", " + dblY
	                              + "], ";
	                        }

	                        //GeoJSON頂点（5頂点ごとに改行）
	                        if (j % 5 == 4)
	                        {
	                            if (fileShapeType == 3)
	                            	p_writer.println("\t\t\t\t\t" + sLine);
	                            else if (fileShapeType == 5)
	                            	p_writer.println("\t\t\t\t\t\t" + sLine);
	                            sLine = "";
	                        }
	                        else if (j == numPoints - 1)
	                        {
	                            if (fileShapeType == 3)
	                            	p_writer.println("\t\t\t\t\t" + sLine);
	                            else if (fileShapeType == 5)
	                            	p_writer.println("\t\t\t\t\t\t" + sLine);
	                        }
						}
					}
					else if (numParts > 1) {
						for (int k = 0; k < numParts; k++) {
							if (k == numParts - 1) {
								String sLine = "";
								//最後の点列
								int startPos = 0;
								int endPos = numPoints - (vStartPos.get(k) + 1);
								for (int l = startPos; l <= endPos; l++) {
									double dblX = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
									double dblY = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
									fileNowLength += 8;
									/*
									System.out.println("X[" + (l + vStartPos.get(k)) + "]：" + dblX);
									System.out.println("Y[" + (l + vStartPos.get(k)) + "]：" + dblY);
									*/

									System.out.println("X[" + (l + vStartPos.get(k)) + "] " + dblX);
									System.out.println("Y[" + (l + vStartPos.get(k)) + "] " + dblY);


									//最終頂点
			                        if (l == endPos)
			                        {
			                            sLine += "[" + dblX
			                              + ", " + dblY
			                              + "]";
			                        }
			                        //最終頂点以外の頂点
			                        else
			                        {
			                            sLine += "[" + dblX
			                              + ", " + dblY
			                              + "], ";
			                        }

			                        //GeoJSON頂点（5頂点ごとに改行）
			                        if (l % 5 == 4)
			                        {
			                            if (fileShapeType == 3)
			                            	p_writer.println("\t\t\t\t\t" + sLine);
			                            else if (fileShapeType == 5)
			                            	p_writer.println("\t\t\t\t\t\t" + sLine);
			                            sLine = "";
			                        }
			                        else if (l == endPos)
			                        {
			                            if (fileShapeType == 3)
			                            	p_writer.println("\t\t\t\t\t" + sLine);
			                            else if (fileShapeType == 5)
			                            	p_writer.println("\t\t\t\t\t\t" + sLine);
			                        }
								}
							}
							else {
								String sLine = "";
								//最初、中間の点列
								int startPos = 0;
								int endPos = vStartPos.get(k + 1) - (vStartPos.get(k) + 1);
								for (int l = startPos; l <= endPos; l++) {
									double dblX = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
									double dblY = byte2Double(reverse8ByteDouble(double2Byte(dis.readDouble())));
									fileNowLength += 8;
									/*
									System.out.println("X[" + (l + vStartPos.get(k)) + "]：" + dblX);
									System.out.println("Y[" + (l + vStartPos.get(k)) + "]：" + dblY);
									*/

									System.out.println("X[" + (l + vStartPos.get(k)) + "] " + dblX);
									System.out.println("Y[" + (l + vStartPos.get(k)) + "] " + dblY);


									//最終頂点
			                        if (l == endPos)
			                        {
			                            sLine += "[" + dblX
			                              + ", " + dblY
			                              + "]";
			                        }
			                        //最終頂点以外の頂点
			                        else
			                        {
			                            sLine += "[" + dblX
			                              + ", " + dblY
			                              + "], ";
			                        }

			                        //GeoJSON頂点（5頂点ごとに改行）
			                        if (l % 5 == 4)
			                        {
			                            if (fileShapeType == 3)
			                            	p_writer.println("\t\t\t\t\t" + sLine);
			                            else if (fileShapeType == 5)
			                            	p_writer.println("\t\t\t\t\t\t" + sLine);
			                            sLine = "";
			                        }
			                        else if (l == endPos)
			                        {
			                            if (fileShapeType == 3)
			                            	p_writer.println("\t\t\t\t\t" + sLine);
			                            else if (fileShapeType == 5)
			                            	p_writer.println("\t\t\t\t\t\t" + sLine);
			                        }

			                        if (l == endPos)
			                        {
			                        	//GeoJSON図形フッタ
			            				p_writer.println("\t\t\t\t\t],");
			            				//GeoJSON図形ヘッダ
			            				p_writer.println("\t\t\t\t\t[");
			                        }
								}
							}
						}
					}
	            }

				//GeoJSON図形フッタ
				if (fileShapeType == 1) {
					p_writer.println("\t\t\t}");
				}
				else if (fileShapeType == 3) {
					p_writer.println("\t\t\t\t]");
					p_writer.println("\t\t\t}");
				}
				else if (fileShapeType == 5) {
					p_writer.println("\t\t\t\t\t]");
					p_writer.println("\t\t\t\t]");
					p_writer.println("\t\t\t}");
				}

	            //GeoJSONポリゴンフッタ
	            //最終ポリゴン
	            if (fileNowLength == fileLength)
	            {
	            	p_writer.println("\t\t}");
	            }
	            //最終ポリゴン以外のポリゴン
	            else
	            {
	            	p_writer.println("\t\t},");
	            }
				recordNowCount++;
			}

	        //GeoJSONフッタ
			p_writer.println("\t]");
			p_writer.println("}");

			/*
			System.out.println("ファイル長：" + fileLength);
			System.out.println("実際のファイル長：" + fileNowLength);
			System.out.println("レコード番号：" + fileRecordCount);
			System.out.println("実際のレコード数：" + recordNowCount);
			*/

			System.out.println("File Length " + fileLength);
			System.out.println("Real File Length " + fileNowLength);
			System.out.println("Record Number " + fileRecordCount);
			System.out.println("Real Record Number " + recordNowCount);


		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {
				//入力ストリームを閉じる
				dis.close();
				p_writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static Dbffile[] readDbf (String dbfFile) {

		BufferedInputStream bi = null;
		DataInputStream dis = null;
//		PrintWriter p_writer = null;
		Dbffile[] dbfFields = null;

		try {
			//インスタンス生成
			bi = new BufferedInputStream(new FileInputStream(dbfFile));

			//インスタンス生成
			dis = new DataInputStream(bi);

//			p_writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\GeoJSONFactory\\P12-14_01_GML\\P12c-14_01.json"),"UTF-8")));

			//---メイン・ファイル・ヘッダ---
			byte fileInfo = dis.readByte();
			//ファイル長の値はワード単位（16 ビットを１ワードとする）で、ヘッダの 50 ワードを含む全ファイルの長さです。
			byte fileLastUpdate1 = dis.readByte();
			byte fileLastUpdate2 = dis.readByte();
			byte fileLastUpdate3 = dis.readByte();
			int fileRecordNum = byte2Int(reverse4ByteInt(int2Byte(dis.readInt())));
			short fileHeaderBytes = byte2Short(reverse2ByteShort(short2Byte(dis.readShort())));
			short fileRecordBytes = byte2Short(reverse2ByteShort(short2Byte(dis.readShort())));
			skipReadByte(20, dis);

			/*
			//表示
			System.out.println("---dBASE IV 2.0 テーブルのファイル・ヘッダ---");
			System.out.println("ファイル情報：" + fileInfo);
			System.out.println("最終更新日：" + String.format("%02d", fileLastUpdate1) + String.format("%02d", fileLastUpdate2) + String.format("%02d", fileLastUpdate3));
			System.out.println("レコード数：" + fileRecordNum);
			System.out.println("ヘッダのバイト数：" + fileHeaderBytes);
			System.out.println("レコードのバイト数：" + fileRecordBytes);
			*/

			//表示
			System.out.println("---dBASE IV 2.0 テーブルのファイル・ヘッダ---");
			System.out.println("Valid dBASE IV file " + fileInfo);
			System.out.println("Last Update " + String.format("%02d", fileLastUpdate1) + String.format("%02d", fileLastUpdate2) + String.format("%02d", fileLastUpdate3));
			System.out.println("Num Records " + fileRecordNum);
			System.out.println("Bytes of Header" + fileHeaderBytes);
			System.out.println("Bytes of Records" + fileRecordBytes);


			int fieldNum = (fileHeaderBytes - 33) / 32;
			dbfFields = new Dbffile[fieldNum];
			for (int i = 0; i < fieldNum; i++) {
				dbfFields[i] = new Dbffile();
				readFields(dis, dbfFields[i]);
			}

			//フィールドの終わりを示す符号（0DH）
			skipReadByte(1, dis);

			for (int j = 0; j < fileRecordNum; j++) {
				/*
				 * データ・レコードの先頭の１バイトが
				 * 半角空白（20H）のとき、このレコードは削除されていないことをあらわす。アスタリスク（2AH）の
				 * とき、 このレコードは削除されていることをあらわす。
				 * */
				skipReadByte(1, dis);
				for (int k = 0; k < fieldNum; k++) {
					int fieldLength = dbfFields[k].getFieldLength();
					byte[] valueByte = new byte[fieldLength];
					for (int l = 0; l < fieldLength; l++) {
						valueByte[l] = dis.readByte();
					}
					String strValue = "";
					if (dbfFields[k].getFieldType().equals("N"))
						strValue = new String(valueByte, "US-ASCII");
					else if (dbfFields[k].getFieldType().equals("C"))
						strValue = new String(valueByte, "SJIS");
					List<String> listValues = dbfFields[k].getListValues();
					listValues.add(strValue.trim());
					dbfFields[k].setListValues(listValues);
				}
			}
			/*
			 * ファイルの末尾には１バイトの end-of-file 符号（ASCII 26、1AH）がある。
			 * */
			skipReadByte(1, dis);

		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {
				//入力ストリームを閉じる
				dis.close();
//				p_writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

        return dbfFields;
	}

	private static void readFields(DataInputStream recdis, Dbffile recDbfFields) {
		List<Byte> listFieldNameBytes = new ArrayList<Byte>();
		String fieldName = "";

		for (int i = 0; i < 11; i++) {
			try {
				byte fieldNameChar = recdis.readByte();
				if (fieldNameChar == 0) {
					skipReadByte(10 - i, recdis);
					break;
				}
				else
					listFieldNameBytes.add(fieldNameChar);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int fieldNameSize = listFieldNameBytes.size();
		byte[] fieldNameBytes = new byte[fieldNameSize];
		for (int j = 0; j < fieldNameSize; j++) {
			fieldNameBytes[j] = listFieldNameBytes.get(j);
		}

		try {
			fieldName = new String(fieldNameBytes, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte fieldTypeByte[] = {0};
		String fieldType = "";
		try {
			fieldTypeByte[0] = recdis.readByte();
			fieldType = new String(fieldTypeByte, "US-ASCII");
		} catch (IOException e) {
			e.printStackTrace();
		}

		skipReadByte(4, recdis);

		byte fieldLengthByte = 0;
		int fieldLength = 0;
		try {
			fieldLengthByte = recdis.readByte();
			fieldLength = Byte.toUnsignedInt(fieldLengthByte);
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte fieldDecimalpartLengthByte = 0;
		int fieldDecimalpartLength = 0;
		try {
			fieldDecimalpartLengthByte = recdis.readByte();
			fieldDecimalpartLength = Byte.toUnsignedInt(fieldDecimalpartLengthByte);
		} catch (IOException e) {
			e.printStackTrace();
		}

		recDbfFields.setFieldName(fieldName);
		recDbfFields.setFieldType(fieldType);
		recDbfFields.setFieldLength(fieldLength);
		recDbfFields.setFieldDecimalpartLength(fieldDecimalpartLength);

		/*
		System.out.println("フィールド名：" + fieldName);
		System.out.println("フィールド型：" + fieldType);
		System.out.println("フィールド長：" + fieldLength);
		System.out.println("小数部の長さ：" + fieldDecimalpartLength);
		*/

		System.out.println("Field Name " + fieldName);
		System.out.println("Field Type " + fieldType);
		System.out.println("Field Length " + fieldLength);
		System.out.println("Field Decimalpart Length " + fieldDecimalpartLength);


		skipReadByte(14, recdis);
	}
}

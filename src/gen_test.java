/*
 * this program generates a 3-dimension byte array and put the array in bip, bil, bsq format in the file
 */

import java.io.*;
/* for details on bip, bil, bsq, go to "http://webhelp.esri.com/arcgisdesktop/9.2/index.cfm?id=2527&pid=2519&topicname=BIL,_BIP,_and_BSQ_raster_files"

The data type: the binary data codification.
◦ 1: 1-byte unsigned integer
◦ 2: 2-byte signed integer
◦ 3: 4-byte signed integer
◦ 4: 4-byte float
◦ 5: 8-byte double
◦ 9: 2x8-byte complex number made up from 2 doubles
◦ 12: 2-byte unsigned integer

byteorder: 0 for little endian, 1 for big endian
*/
public class gen_test{
	//params: modify the params here to test different cases
	static int lines=4,samples=4,bands=3,datatype=2,datatype_size=2;
	static int byteorder=1;
	// data array
	static byte[][][] arr=new byte[lines][samples][bands*datatype_size]; // 3 dimensions: line, sample, and bands
	public static void main(String Args[]) throws IOException{
		int tmp=0;
		System.out.println("lines: "+lines+", samples: "+samples+", bands: "+bands+", datatype:"+datatype+",  datatype_size: "+datatype_size);
		System.out.println("arr["+lines+"]["+samples+"]["+bands+"]");
		for(int i=0;i<lines;i++){
			System.out.println();
			for(int j=0;j<samples;j++){
				System.out.println();
				for(int k=0;k<bands*datatype_size;k++){
					arr[i][j][k]=(byte)(++tmp);//modify here to test different arrays
					System.out.print(" arr["+i+"]["+j+"]["+k+"]=" +arr[i][j][k]+"\n");
				}
			}
		}
		write_hdr("bip");
		write_hdr("bil");
		write_hdr("bsq");
		
		write_bip();
		 write_bil();
		 write_bsq();

	}

	private static void write_bip() throws IOException {
		// TODO Auto-generated method stub
		DataOutputStream bip = new DataOutputStream(new FileOutputStream("bip"));
		for(int i=0;i<lines;i++){
			for(int j=0;j<samples;j++){
				for(int k=0;k<bands*datatype_size;k++){
					bip.write(arr[i][j][k]);
				}
			}
		}
		bip.flush();bip.close();
	}
	private static void write_bil() throws IOException {
		// TODO Auto-generated method stub
		DataOutputStream bil = new DataOutputStream(new FileOutputStream("bil"));
		for(int i=0;i<lines;i++){
			for(int k=0;k<bands;k++){
				for(int j=0;j<samples;j++){
					for(int s=0;s<datatype_size;s++)
						bil.write(arr[i][j][k*datatype_size+s]);
				}
			}
		}
		bil.flush();bil.close();
	}
	private static void write_bsq() throws IOException {
		// TODO Auto-generated method stub
		 DataOutputStream bsq = new DataOutputStream(new FileOutputStream("bsq"));
		for(int k=0;k<bands;k++){
			for(int i=0;i<lines;i++){
				for(int j=0;j<samples;j++){
					for(int s=0;s<datatype_size;s++)
						bsq.write(arr[i][j][k*datatype_size+s]);
				
				}
			}
		}
		bsq.flush();bsq.close();
	}

	private static void write_hdr(String filetype) throws IOException {//filetype="bip", "bil","bsq"
		// TODO Auto-generated method stub
		BufferedWriter br=new BufferedWriter(new FileWriter(new File(filetype+".hdr")));
		br.write ("samples = "+samples);
		br.write("\nlines =  "+lines);
		br.write("\nbands = "+bands);
		br.write("\nheader offset = " +0);
		br.write("\ndata type = "+datatype);
		br.write("\ninterleave = "+filetype);
		br.write("\nbyte order = "+byteorder);
		br.flush();br.close();

	}
}

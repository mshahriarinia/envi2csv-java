import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
//input *.hdr file name
// output *.csv file
//csv file format: line,sample,band,

/* for details on bip, bil, bsq, go to "http://webhelp.esri.com/arcgisdesktop/9.2/index.cfm?id=2527&pid=2519&topicname=BIL,_BIP,_and_BSQ_raster_files"

The data type: the binary data codification.
◦ 1: 1-byte unsigned integer
◦ 2: 2-byte signed integer
◦ 3: 4-byte signed integer
◦ 4: 4-byte float
◦ 5: 8-byte double
◦ 9: 2x8-byte complex number made up from 2 doubles
◦ 12: 2-byte unsigned integer
*/
//FIXME
//unsigned data not processed
public class envi2csv{
	static long num_lines=0,num_samples=0,num_bands=0,headoffset;
	static int data_type=-1;
	static int data_size_per_cell=0;//num of bytes each value occupies
	static String interleave;
	static int byte_order=0;//0 for little endian;
	static double wavelenght[];
	static boolean omit_dimension_indicates=false;
	
	public static void main(String[] args) throws IOException{
		if(args.length<1){
			System.out.println("Please specify the hdr file name!");
			System.exit(0);
		}
		System.out.println(args[0]);
		String hdrfile=args[0];//"E:/neondata/f100910t01p00r02rdn_b_NEON-L1B/f100910t01p00r02rdn_b_Refl_PostFLAASH_water.dat.hdr";
		//read .hdr file to get the basic param
		read_hdr(hdrfile);
		
		//read img file and convert the data to csv format
		//3d array envidata[num_lines][num_samples][num_bands];
		if(interleave.equalsIgnoreCase("BIP")){
			read_image_bip(hdrfile.substring(0, hdrfile.length()-4));
		}
		else if(interleave.equalsIgnoreCase("BIL")){
			read_image_bil(hdrfile.substring(0, hdrfile.length()-4));
		}
		else if(interleave.equalsIgnoreCase("BSQ")){
			read_image_bsq(hdrfile.substring(0, hdrfile.length()-4));
		}
		
	}
	
	private static void read_image_bip(String imgfile) throws IOException {
		
		RandomAccessFile img=new RandomAccessFile(imgfile,"r");
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File(imgfile+".csv")));
		bw.write("line_no,sample_no,band_no,value");
		byte[] data_a_line=new byte[(int) (num_samples*num_bands*data_size_per_cell)];
		
		for(int line_no=0;line_no<num_lines;line_no++){
			img.seek(line_no*num_samples*num_bands*data_size_per_cell+headoffset);
			int readcount=img.read(data_a_line, 0, (int) (num_samples*num_bands*data_size_per_cell));
		
			System.out.println("line no:"+line_no+"  read count:"+readcount);
			if(readcount!=num_samples*num_bands*data_size_per_cell){
				System.out.println("Not enough inputs of img, img file may be broken!");
				System.exit(-1); 
			}
			ByteBuffer bb=ByteBuffer.wrap(data_a_line);
			
			if(byte_order==0){//little endian
				bb.order(ByteOrder.LITTLE_ENDIAN);
			}else{
				bb.order(ByteOrder.BIG_ENDIAN);
			}
			for(int sample_no=0;sample_no<num_samples;sample_no++){
				for(int band_no=0;band_no<num_bands;band_no++){
				//	System.out.println(line_no+"  "+sample_no+" "+band_no+"  "+bb.capacity());
					String value="";
					//FIXME
					switch (data_type){
					case 1:  break;//value=Integer.toString(bb.get((int) ((sample_no*num_samples+band_no)*data_size_per_cell)));break;
					case 2: value=Integer.toString(bb.getShort((int) ((sample_no*num_bands+band_no)*data_size_per_cell)));break;
					case 3: value=Integer.toString(bb.getInt((int) ((sample_no*num_bands+band_no)*data_size_per_cell)));break;
					case 4: value=Float.toString(bb.getFloat((int) ((sample_no*num_bands+band_no)*data_size_per_cell)));break;
					case 5: value=Double.toString(bb.getDouble((int) ((sample_no*num_bands+band_no)*data_size_per_cell)));break;
					case 9: data_size_per_cell=16;break;
					case 12: data_size_per_cell=2;break;
					}
					if(omit_dimension_indicates==false){
						bw.write("\nl"+line_no+",s"+sample_no+",b"+band_no+","+value);
					}
					else{
						bw.write("\n"+value);
					}
					
				}
			}
		}
		bw.flush();bw.close();
		img.close();
	}
	private static void read_image_bil(String imgfile) throws IOException {
		// TODO Auto-generated method stub
		//read img file, line by line into csv file, bip
		RandomAccessFile img=new RandomAccessFile(imgfile,"r");
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File(imgfile+".csv")));
		bw.write("line_no,sample_no,band_no,value");
		byte[] data_a_line=new byte[(int) (num_samples*num_bands*data_size_per_cell)];
		
		for(int line_no=0;line_no<num_lines;line_no++){
			img.seek(line_no*num_samples*num_bands*data_size_per_cell+headoffset);
			int readcount=img.read(data_a_line, 0, (int) (num_samples*num_bands*data_size_per_cell));
		
			System.out.println("line no:"+line_no+"  read count:"+readcount);
			if(readcount!=num_samples*num_bands*data_size_per_cell){
				System.out.println("Not enough inputs of img, img file may be broken!");
				System.exit(-1); 
			}
			ByteBuffer bb=ByteBuffer.wrap(data_a_line);
			
			if(byte_order==0){//little endian
				bb.order(ByteOrder.LITTLE_ENDIAN);
			}else{
				bb.order(ByteOrder.BIG_ENDIAN);
			}
			for(int sample_no=0;sample_no<num_samples;sample_no++){
				for(int band_no=0;band_no<num_bands;band_no++){
				//	System.out.println(line_no+"  "+sample_no+" "+band_no+"  "+bb.capacity());
					String value="";
					//FIXME
					switch (data_type){
					case 1:  break;//value=Integer.toString(bb.get((int) ((sample_no*num_samples+band_no)*data_size_per_cell)));break;
					case 2: value=Integer.toString(bb.getShort((int) ((sample_no+band_no*num_samples)*data_size_per_cell)));break;
					case 3: value=Integer.toString(bb.getInt((int) ((sample_no+band_no*num_samples)*data_size_per_cell)));break;
					case 4: value=Float.toString(bb.getFloat((int) ((sample_no+band_no*num_samples)*data_size_per_cell)));break;
					case 5: value=Double.toString(bb.getDouble((int) ((sample_no+band_no*num_samples)*data_size_per_cell)));break;
					case 9: data_size_per_cell=16;break;
					case 12: data_size_per_cell=2;break;
					}
					if(omit_dimension_indicates==false){
						bw.write("\nl"+line_no+",s"+sample_no+",b"+band_no+","+value);
					}
					else{
						bw.write("\n"+value);
					}
				}
			}
		}
		bw.flush();bw.close();
		img.close();
	}
	private static void read_image_bsq(String imgfile) throws IOException {
		// TODO Auto-generated method stub
		//read img file, line by line into csv file, bsq
		RandomAccessFile img=new RandomAccessFile(imgfile,"r");
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File(imgfile+".csv")));
		bw.write("line_no,sample_no,band_no,value");
		byte[] data_a_line=new byte[(int) (num_samples*num_bands*data_size_per_cell)];
		
		for(int line_no=0;line_no<num_lines;line_no++){
			//read a line equivalent to bil
			for(int band_no=0;band_no<num_bands;band_no++){
				img.seek((band_no*num_lines*num_samples+line_no*num_samples)*data_size_per_cell+headoffset);
				int readcount=img.read(data_a_line, (int) (band_no*num_samples*data_size_per_cell), (int) (num_samples*data_size_per_cell));
				System.out.println("line no:"+line_no+"  read count:"+readcount);
				if(readcount!=num_samples*data_size_per_cell){
					System.out.println("seek"+((band_no*num_lines*num_samples+line_no*num_samples)*data_size_per_cell+headoffset));
					System.out.println("Not enough inputs of img, img file may be broken!"+(num_samples*num_bands*data_size_per_cell));
					System.exit(-1); 
				}
			}
		
			ByteBuffer bb=ByteBuffer.wrap(data_a_line);
			
			if(byte_order==0){//little endian
				bb.order(ByteOrder.LITTLE_ENDIAN);
			}else{
				bb.order(ByteOrder.BIG_ENDIAN);
			}
			for(int sample_no=0;sample_no<num_samples;sample_no++){
				for(int band_no=0;band_no<num_bands;band_no++){
					String value="";
					//FIXME
					switch (data_type){
					case 1:  break;//value=Integer.toString(bb.get((int) ((sample_no*num_samples+band_no)*data_size_per_cell)));break;
					case 2: value=Integer.toString(bb.getShort((int) ((sample_no+band_no*num_samples)*data_size_per_cell)));break;
					case 3: value=Integer.toString(bb.getInt((int) ((sample_no+band_no*num_samples)*data_size_per_cell)));break;
					case 4: value=Float.toString(bb.getFloat((int) ((sample_no+band_no*num_samples)*data_size_per_cell)));break;
					case 5: value=Double.toString(bb.getDouble((int) ((sample_no+band_no*num_samples)*data_size_per_cell)));break;
					case 9: data_size_per_cell=16;break;
					case 12: data_size_per_cell=2;break;
					}
					if(omit_dimension_indicates==false){
						bw.write("\nl"+line_no+",s"+sample_no+",b"+band_no+","+value);
					}
					else{
						bw.write("\n"+value);
					}
				}
			}
		}
		bw.flush();bw.close();
		img.close();
	}


	private static void read_hdr(String hdr_file) throws IOException {
		// TODO Auto-generated method stub
		//read hdr file and init  keys
		BufferedReader br=new BufferedReader(new FileReader(new File(hdr_file)));
		String inline="";
		while((inline=br.readLine())!=null){
			//System.out.println(inline);
			String[] split=inline.split("=");
			if(split.length>=2){
				switch(split[0].trim()){
				case "samples": {
					num_samples=Integer.parseInt(split[1].trim());
					break;
				}
				case "lines":{
					num_lines=Integer.parseInt(split[1].trim());
					break;
				}
				case "bands":{
					num_bands=Integer.parseInt(split[1].trim());
					break;
				}
				case "header offset":{
					headoffset=Integer.parseInt(split[1].trim());
					break;
				}
				case "data type":{
					data_type=Integer.parseInt(split[1].trim());
					break;
					}
				case "interleave":{
					interleave=split[1].trim();
					break;
				}
				case "byte order":{
					byte_order=Integer.parseInt(split[1].trim());
					break;
				}
				case "wavelength":{
					break;
				}
				}
				
			}
		}
		switch (data_type){
		case 1: data_size_per_cell=1;break;
		case 2: data_size_per_cell=2;break;
		case 3: data_size_per_cell=4;break;
		case 4: data_size_per_cell=4;break;
		case 5: data_size_per_cell=8;break;
		case 9: data_size_per_cell=16;break;
		case 12: data_size_per_cell=2;break;
		}
		br.close();
	}
}

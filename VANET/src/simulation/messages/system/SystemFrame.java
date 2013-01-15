package simulation.messages.system;

import java.nio.ByteBuffer;

import simulation.messages.system.custom.AgentInformationReplySystemFrame;
import simulation.messages.system.custom.AgentInformationRequestSystemFrame;
import simulation.messages.system.custom.PerceivedAgentsInformationReplySystemFrame;
import simulation.messages.system.custom.PerceivedAgentsInformationRequestSystemFrame;
import simulation.utils.BytesArray;

public class SystemFrame {

	private final static  short HEADER 	= 0x01FF;
	private final static short TAILER 	= 0x02FF;
	private final static byte INSERTION = 0x00;

	protected byte functionCode;
	protected byte subFunctionCode;
	private byte[] data;

	public SystemFrame(byte[] bytes) throws NotSystemFrameException
	{
		byte _HEADER1 = (byte) ((SystemFrame.HEADER&0xFF00)>>8);
		byte _HEADER2 = (byte)  (SystemFrame.HEADER&0x00FF);
		byte _TAILER1 = (byte) ((SystemFrame.TAILER&0xFF00)>>8);
		byte _TAILER2 = (byte)  (SystemFrame.TAILER&0x00FF);

		if(_HEADER2!=_TAILER2) System.err.println("\n!!! WARNING: header and tailer must have the same LSB");

		if (bytes.length<2 /* header */ +2 /* size */ +2 /* chksum */ +2 /* tailer */) throw new NotSystemFrameException("Runt frame");

		ByteBuffer buf=ByteBuffer.wrap(bytes);

		// Extract header
		if(buf.getShort()!=SystemFrame.HEADER) throw new NotSystemFrameException("Bad header");

		// Extract frame length (and perform basic tests)
		short dataLength=buf.getShort();
		if(dataLength>bytes.length || dataLength<0) throw new NotSystemFrameException("Bad size ("+dataLength+")");

		// Extract information field of the frame (and suppress the  0x00 bytes before 0xFF bytes)
		byte[] info = new byte[dataLength];
		int i; int index=0;	byte next; boolean insertion;
		try
		{
			while(index<dataLength)
			//for(i=0;i<bytes.length-2 /* header */ -2 /* size */ -2 /* chksum */ -2 /* tailer */;i++) 
			{
				info[index++]=buf.get();
				System.out.println(">>> index="+index+" position dans buffer="+buf.position()+"  readen byte="+info[index-1]);
				if(info[index-1]==SystemFrame.INSERTION)
				{
					next = buf.get();
					if(next==_HEADER2 || next==_TAILER2) 
						info[index-1]=next;
					else
						buf.position(buf.position()-1);

				}
				else if(info[index-1]==_HEADER1 || info[index-1]==_TAILER1)
				{
					int pos = buf.position();
					next = buf.get();
					if(info[index-1]==_HEADER1 && next==_HEADER2)
						throw new NotSystemFrameException("Header found in data");
					else if(info[index-1]==_TAILER1 && next==_TAILER2)
						throw new NotSystemFrameException("Tailer found in data");
		
					buf.position(pos);
				}
				else if(info[index-1]==_HEADER2 || info[index-1]==_TAILER2)
					throw new NotSystemFrameException("header/tailer LSByte not preceded by a INSERTION byte found in data");
				else
				{/* Nothing to do */}
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			throw new NotSystemFrameException("Probably error in information field length");
		}



		System.out.println("DATA ARE "+BytesArray.displayByteArray(info));
		// Check the data length given by the frame field and the real extracted data bytes
		if(index!=dataLength)throw new NotSystemFrameException("Bad size (Length in data frame:"+dataLength+" , Real extracted number of bytes="+index+")   == Extracted data:"+BytesArray.displayByteArray(info));

		// Extract and check the chksum
		short readChksum = buf.getShort();
		long realChksum = SystemFrame.chksum(ByteBuffer.allocate(2+info.length).putShort(dataLength).put(info).array());
		if (realChksum != readChksum) throw new NotSystemFrameException("Bad checksum (read in frame = "+readChksum+" , computed with data = "+realChksum+")");

		// Extract tailer
		if(buf.getShort()!=SystemFrame.TAILER)throw new NotSystemFrameException("Bad tailer");

		// Interpretation des données
		ByteBuffer finalData = ByteBuffer.wrap(info);
		this.functionCode=finalData.get();
		this.subFunctionCode=finalData.get();
		this.data=new byte[info.length-2];
		for(i=0;i<info.length-1-1;i++) this.data[i]=finalData.get();
	}
	public SystemFrame(byte functionCode, byte subFunctionCode)
	{
		this(functionCode,subFunctionCode,null);
	}

	public SystemFrame(byte functionCode, byte subFunctionCode, byte[] data)
	{
		this.functionCode=functionCode;
		this.subFunctionCode=subFunctionCode;
		this.data=data;
	}

	public void setFunctionCode(byte functionCode)
	{
		this.functionCode=functionCode;
	}

	public void setSubFunctionCode(byte subFunctionCode)
	{
		this.subFunctionCode=subFunctionCode;
	}

	public byte getFunctionCode()
	{
		return this.functionCode;
	}

	public byte getSubFunctionCode()
	{
		return this.subFunctionCode;
	}
	public final byte[] getData()
	{
		return this.data;
	}

	public final static short chksum(byte[] data)
	{
		long sum = 0;
		for(int i=0;i<data.length;i++) sum+=(data[i]>=0 ? data[i] : 256+data[i]) ;
		return (short) (sum&0x0000FFFF);
	}



	private final static byte[] checkFrameToSend(byte[] info) 
	{
		byte _HEADER2 = (byte)  (SystemFrame.HEADER&0x00FF);
		byte _TAILER2 = (byte)  (SystemFrame.TAILER&0x00FF);

		int LSBoccurence = 0;
		// Count the LSBs occurence (header and tailer)
		for(int i=0;i<info.length;i++)
			if(info[i]==_HEADER2 || info[i]==_TAILER2) LSBoccurence++;

		// Allocate the new buffer
		ByteBuffer bytes = ByteBuffer.allocate(info.length+LSBoccurence);
		for(int i=0;i<info.length;i++)
		{
			if(info[i]==_HEADER2 || info[i]==_TAILER2) bytes.put((byte)0x00);
			bytes.put(info[i]);
		}

		return bytes.array();
	}


	public final byte[] toBytes(byte[] data)
	{
		//Entête     Taille trame      Code fonction      Code Ss-fonction    Data   Chksum   Enqueue
		//  16b           16b               8b                   8b            nb      16b      16b

		byte[] info = ByteBuffer.allocate(1+1+data.length).put(this.functionCode).put(this.subFunctionCode).put(data).array();
		System.out.println("INFO="+BytesArray.displayByteArray(info)+"   LENGTH="+Integer.toHexString(info.length)+"   ("+info.length+")");
		short chksum = SystemFrame.chksum(ByteBuffer.allocate(2+info.length).putShort((short)info.length).put(info).array());
		System.out.println("CHKSUM="+Integer.toHexString(chksum));

		info=SystemFrame.checkFrameToSend(info);
		System.out.println("CHECKED INFO"+BytesArray.displayByteArray(info));
		byte[] frame = ByteBuffer.allocate(2+2+2+info.length+2).putShort(SystemFrame.HEADER).putShort((short)info.length).put(info).putShort(chksum).putShort(SystemFrame.TAILER).array();
		System.out.println("SENDED FRAME="+BytesArray.displayByteArray(frame));
		return frame;

	}
	public byte[] toBytes()
	{
		return this.toBytes(new byte[0]);
	}

	public String toString()
	{
		return "Function code:"+Byte.toString(this.functionCode)+" Subfunction code:"+Byte.toString(this.subFunctionCode)+" Data:"+BytesArray.displayByteArray(this.data);
	}

	public final SystemFrame getInterpretedSystemFrame() throws NotUnderstantableSystemFrameException
	{
		switch(this.functionCode)
		{
		case 0x01:
			switch(this.subFunctionCode)
			{
			case 0x01:
				return new AgentInformationRequestSystemFrame(this.data);
			case 0x02:
				return new PerceivedAgentsInformationRequestSystemFrame(this.data);
			default:
				throw new NotUnderstantableSystemFrameException(this.functionCode,this.subFunctionCode,"No implemented interpretation.");
			}
		case 0x02:
			switch(this.subFunctionCode)
			{
			case 0x01:
				return new AgentInformationReplySystemFrame(this.data);
			case 0x02:
				return new PerceivedAgentsInformationReplySystemFrame(this.data);
			default:
				throw new NotUnderstantableSystemFrameException(this.functionCode,this.subFunctionCode,"No implemented interpretation.");
			}
		default:
			return null;
		}
	}

}

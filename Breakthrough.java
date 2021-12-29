import javax.imageio.*;
import javax.swing.*;
import java.io.File;
import java.io.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;

public class Breakthrough
{
	static public int now=-1;
	static public int prev=-1;
	static public int turn =1;
	static public int board_size =64;
	static public Block[]b=new Block[board_size];
	static public int[] pos_score={999,999,999,999,999,999,999,999,
					8,8,7,7,7,7,8,8,
					7,7,6,6,6,6,7,7,
					5,6,6,5,5,6,6,5,
					4,4,5,5,5,5,4,4,
					3,3,4,4,4,4,3,3,
					2,2,3,3,3,3,2,2,
					1,1,2,2,2,2,1,1};
	public static void main(String[] args)
	{
		
		JFrame frame=new JFrame("Breakthrough");
		frame.setLayout(null);
		frame.setSize(656,678);
		
		
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		BufferedImage[] img=new BufferedImage[4];
		try
        {
			for(int i=0;i<4;i++)
			{
			String a=new String(i+".jpg");
			img[i]=ImageIO.read(new File(a));
			}
		}
		catch(Exception e)
		{System.out.println("Cannot load image");
		}
		for(int i=0;i<board_size;i++)
		{
			if(i<16)
				b[i]=new Block(i,-1);//white
			else if (i>47)
				b[i]=new Block(i,1);//black
			else
				b[i]=new Block(i);
			b[i].setBounds(i%8*80,i/8*80,80,80);
			if(b[i].status>=0)
			b[i].setIcon(new ImageIcon(img[b[i].status]));
			else
				b[i].setIcon(new ImageIcon(img[2]));
			frame.add(b[i]);
			b[i].addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					Block btn = (Block) ae.getSource();
					if(now!=-1&&btn.status!=b[now].status)//selected a chess and selecting move
					{
						prev = now;
						now = btn.index;
							if(check_step_available(b,prev,now))
						{
							b[now].status=b[prev].status;
							b[prev].status=0;
							
							if(now/8==0||now/8==7)
							{
								if(turn==1)
								JOptionPane.showMessageDialog(null, "山豬國獲勝");
								else
									JOptionPane.showMessageDialog(null, "天龍國獲勝");
								turn=8;
							}
							else
							turn*=-1;
						}
						else
						{
							/*do nothing*/
						}
						now=-1;
						for(int i=0;i<board_size;i++)//select a chess
						{
							if(b[i].status>=0)
							b[i].setIcon(new ImageIcon(img[b[i].status]));
							else
							b[i].setIcon(new ImageIcon(img[2]));
							b[i].select=false;
						}
					}
					else if(btn.select)//cancel select
					{
						now=-1;
						if(btn.status>=0)
						btn.setIcon(new ImageIcon(img[btn.status]));
						else
						btn.setIcon(new ImageIcon(img[2]));
						btn.select=false;
					}
					else if(btn.status==turn)
					{
						for(int i=0;i<board_size;i++)//select a chess
						{
							if(b[i].select)
							{
							if(b[i].status>=0)
							b[i].setIcon(new ImageIcon(img[b[i].status]));
							else
							b[i].setIcon(new ImageIcon(img[2]));
							}
							b[i].select=false;
						}
						btn.select=true;
						now=btn.index;
						btn.setIcon(new ImageIcon("3.jpg"));
					}
				}
			}
			);
		}
		JFrame controller=new JFrame();
		controller.setBounds(0,0,200,150);
		JButton AI=new JButton("AI走步");
		AI.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				AI_Move(b,turn,1);
				turn*=-1;
				for(int i=0;i<board_size;i++)
				{
					
					if(b[i].status>=0)
					b[i].setIcon(new ImageIcon(img[b[i].status]));
					else
					b[i].setIcon(new ImageIcon(img[2]));
					frame.repaint();
					
				}
			}
		});
		AI.setFont(new Font("新細明體",Font.PLAIN,40));
		AI.setBounds(0,0,200,150);
		controller.add(AI);
		controller.setVisible(true);
        frame.setVisible(true);
	}
	
	static boolean check_step_available(Block[] now_b,int select,int next)
	{
		if(next>63||next<0)return false;
		if(Math.abs(next/8-select/8)!=1)return false;
		if(now_b[next].status==now_b[select].status)return false;
		int temp=Math.abs(select-next);
		if(temp<7||temp>9)
			return false;
		else
		{
			if(temp==8)
			{
				if(now_b[next].status==0)
				return true;
				else
					return false;
			}
			else
				return true;
		}	
	}
	static int find_road(Block[] now_b,int select)
	{
		int num=0;
		for(int i=7;i<10;i++)
		{
			if(check_step_available(now_b,select,select-i*now_b[select].status))
			{	
			num+=1;
			}
		}
		return num;
	}	
	static int count(Block[] now_b,int turn)
	{
		int num=0;
		
		for(int i=0;i<board_size;i++)
		{
			if(now_b[i].status==turn)
			{
				//num+=find_road(now_b,i);
				if(turn==1)
				num+=pos_score[i];
				else
				num+=pos_score[63-i];
			}
		}
		return num;
	}
	static int AI_Move(Block[] now_b,int turn,int level)
	{
		if(level==0)
			return 0;
		int select=0,step=0,score=-99999;
		//simulate click
		Block[] best=copy(now_b);
		for(int i=0;i<board_size;i++)
		{
			if(now_b[i].status==turn)
			{
				//click i button
				for(int j=7;j<10;j++)
				{
				if(check_step_available(now_b,i,i-j*turn))
					{
						int temp=0;
						Block[] a=copy(now_b);
						
						if(now_b[i-j*turn].status==0)
						a[i].status=now_b[i-j*turn].status;
						else
						{a[i].status=0;temp+=50;}
						a[i-j*turn].status=now_b[i].status;
						temp+=count(a,turn);
						if(temp>score)
						{
							System.out.println(temp);
							score=temp;
							best=copy(a);
						}
					}
				}
			}
		}
		for(int i=0;i<board_size;i++)
		{
			now_b[i].status=best[i].status;
		}
		
		return count(best,turn);
	}
	static public Block[] copy(Block[] b)
	{
		Block[] new_b=new Block[board_size];
		for(int i=0;i<board_size;i++)
		{
			new_b[i]=new Block(b[i].index,b[i].status);
		}
		return new_b;
	}
}
class Block  extends JButton//each block of the whole board
{
	int status; //what status is of the block. 0=emepy 1=black -1=white
	boolean select;
	int index;
	boolean counted;
	public Block(int n)
	{
		index=n;
		status=0;
		select=false;
		counted=false;
	}
	public Block(int n,int m)
	{
		index=n;
		status=m;
		select=false;
		//counted=false;
	}
	
}
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace WindowsFormsApplication1
{
    public partial class Form1 : Form
    {
        GraphicsPath currentPath;
        ConcurrentBag<ColorPath> pathList = new ConcurrentBag<ColorPath>();

        List<RectangleF> pointList = new List<RectangleF>();
        Boolean bRunning = true;
        Pen drawPen = new Pen(Brushes.Blue, 2.0F);

        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_MouseDown(object sender, MouseEventArgs e)
        {
            //Point mouseDownLocation = new Point(e.X, e.Y);
            //currentPath.AddLine(mouseDownLocation, mouseDownLocation);

            //this.Focus();
            //this.Invalidate();
        }

        private void Form1_MouseMove(object sender, MouseEventArgs e)
        {
            //int mouseX = e.X;
            //int mouseY = e.Y;

            //currentPath.AddLine(mouseX, mouseY, mouseX, mouseY);
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            currentPath = new System.Drawing.Drawing2D.GraphicsPath();

            receiveWorker.RunWorkerAsync();
        }

        private void Form1_Paint(object sender, PaintEventArgs e)
        {
            foreach (var item in pathList)
            {
                e.Graphics.DrawPath(item.DrawPen, item.GPath);
            }
            e.Graphics.DrawPath(drawPen, currentPath);

            //if (pointList.Count != 0)
            //{
            //    e.Graphics.FillRectangles(Brushes.Red, pointList.ToArray()); 
            //}
        }

        private void drawTimer_Tick(object sender, EventArgs e)
        {
            //Point pt = Cursor.Position;
            //int mouseX = pt.X;
            //int mouseY = pt.Y;

            //currentPath.AddLine(mouseX, mouseY, mouseX, mouseY);

            //this.Focus();
            //this.Invalidate();
        }

        private void Form1_KeyDown(object sender, KeyEventArgs e)
        {
            //if (e.KeyCode == Keys.A)
            //{
            //    pathList.Add(new GraphicsPath(currentPath.PathPoints, currentPath.PathTypes));
            //}
            //else
            //{
            //    pathList.Clear();
            //}

            //currentPath.Dispose();
            //currentPath = new System.Drawing.Drawing2D.GraphicsPath();

            //pointList.Clear();

            while (!pathList.IsEmpty)
            {
                ColorPath dummy;
                pathList.TryTake(out dummy);
            }

            currentPath.Dispose();
            currentPath = new System.Drawing.Drawing2D.GraphicsPath();

            this.Focus();
            this.Invalidate();
        }

        private void btnClear_Click(object sender, EventArgs e)
        {
            currentPath.Dispose();
            currentPath = new System.Drawing.Drawing2D.GraphicsPath();
        }

        private void receiveWorker_DoWork(object sender, DoWorkEventArgs e)
        {
            //Creates a UdpClient for reading incoming data.
            UdpClient receivingUdpClient = new UdpClient(11000);

            //Creates an IPEndPoint to record the IP Address and port number of the sender. 
            // The IPEndPoint will allow you to read datagrams sent from any source.
            IPEndPoint RemoteIpEndPoint = new IPEndPoint(IPAddress.Any, 0);

            float scaleX = (float)this.Width / (float)1300;
            float scaleY = (float)this.Height / (float)700;
            Matrix translateMatrix = new Matrix();
            translateMatrix.Scale(scaleX, scaleY);
            //translateMatrix.Rotate(-90);
            //translateMatrix.Translate(-800, 0);

            while (bRunning)
            {
                try
                {
                    // Blocks until a message returns on this socket from a remote host.
                    Byte[] receiveBytes = receivingUdpClient.Receive(ref RemoteIpEndPoint);
                    PointF[] pointArray = null;
                    string mode = "";
                    string returnData = Encoding.ASCII.GetString(receiveBytes);
                    var dummy = returnData.Split(',');

                    if (dummy.Length == 2)
                    {
                        mode = dummy[0].ToUpper();
                        switch (mode)
                        {
                            case "W":
                                while (!pathList.IsEmpty)
                                {
                                    ColorPath dummyPath;
                                    pathList.TryTake(out dummyPath);
                                }

                                currentPath.Dispose();
                                currentPath = new System.Drawing.Drawing2D.GraphicsPath();
                                break;
                            case "S":
                                drawPen.Color = Color.FromArgb(int.Parse(dummy[1]));
                                break;
                            default:
                                break;
                        }
                    }

                    if (dummy.Length == 3)
                    {
                        mode = dummy[0].ToUpper();
                        float pointX = float.Parse(dummy[1]);
                        float pointY = float.Parse(dummy[2]);

                        pointArray = new PointF[] { new PointF(pointX, pointY) };
                        translateMatrix.TransformPoints(pointArray);
                        pointX = pointArray[0].X;
                        pointY = pointArray[0].Y;

                        switch (mode)
                        {
                            case "D":
                                currentPath.AddLine(pointX, pointY, pointX, pointY);
                                Cursor.Position = new Point((int)pointX, (int)pointY + 16);
                                break;
                            case "H":
                                Cursor.Position = new Point((int)pointX, (int)pointY + 16);
                                break;
                            case "C":
                                //pathList.Add(new GraphicsPath(currentPath.PathPoints, currentPath.PathTypes));
                                pathList.Add(
                                new ColorPath()
                                {
                                    DrawPen = new Pen(drawPen.Brush, drawPen.Width),
                                    GPath = new GraphicsPath(currentPath.PathPoints, currentPath.PathTypes)
                                });
                                currentPath.Dispose();
                                currentPath = new System.Drawing.Drawing2D.GraphicsPath();
                                break;
                            default:
                                break;
                        }

                        //if ((pointX == -1) && (pointY == -1))
                        //{
                        //    pathList.Add(new GraphicsPath(currentPath.PathPoints, currentPath.PathTypes));

                        //    currentPath.Dispose();
                        //    currentPath = new System.Drawing.Drawing2D.GraphicsPath();
                        //}
                        //else
                        //{
                        //    currentPath.AddLine(pointX, pointY, pointX, pointY);

                        //    Cursor.Position = new Point((int)pointX, (int)pointY);
                        //}
                        //pointList.Add(new RectangleF(pointX, pointY, 3,3));
                    }

                    Console.WriteLine("This is the message you received " +
                                                returnData.ToString());
                    //Console.WriteLine("This message was sent from " +
                    //                            RemoteIpEndPoint.Address.ToString() +
                    //                            " on their port number " +
                    //                            RemoteIpEndPoint.Port.ToString());

                    receiveWorker.ReportProgress(0);

                }
                catch (Exception ex)
                {
                    Console.WriteLine(ex.ToString());
                }
            }

        }

        private void receiveWorker_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            this.Focus();
            this.Invalidate();
        }

        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            bRunning = false;
        }

        //private void panel1_Paint(object sender, PaintEventArgs e)
        //{
        //    e.Graphics.DrawPath(System.Drawing.Pens.DarkRed, mousePath);
        //}
    }

    public class ColorPath
    {
        public GraphicsPath GPath;
        public Pen DrawPen;
    }

    public class DBPanel : Panel
    {
        public DBPanel()
        {
            this.DoubleBuffered = true;
        }
    }
}
